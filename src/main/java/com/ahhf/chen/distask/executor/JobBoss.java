package com.ahhf.chen.distask.executor;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import com.ahhf.chen.distask.domain.Job;
import com.ahhf.chen.distask.domain.config.JobConfig;
import com.ahhf.chen.distask.domain.config.ModuleJobConfig;
import com.ahhf.chen.distask.enums.JobStatus;
import com.ahhf.chen.distask.service.JobService;
import com.ahhf.chen.distask.util.DateUtil;
import com.ahhf.chen.distask.util.IPUtil;
import com.ahhf.chen.distask.util.ServiceManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;

/**
 * 类JobBoss.java的实现描述：作业执行的Boss，主要负责查询可执行的作业
 */
@Slf4j
public class JobBoss implements Runnable {

    //每次查询待处理Job的最大数量
    private static final int                   EVERY_QUERY_JOB_MAX_COUNT = 50;

    //失败次数延迟执行时间,key:执行次数，value:延迟执行时间（分钟）
    private static final Map<Integer, Integer> FAIL_JOB_DELAY_TIME_CFG   = Maps.newHashMap();

    //锁定的Job强制释放的时间（分钟）
    private static final Integer               LOCK_JOB_RELEASE_TIME     = 60;

    //Woker执行器数量（默认值：4个）
    private int                                wokerCount                = 4;

    //Boss配置的JobWorker数组（默认4个worker线程）
    private JobWorker[]                        jobWorkers                = null;

    //Boss分配Job任务时选择的Worker执行器索引
    private AtomicInteger                      wokerIndex                = new AtomicInteger(0);

    //随机数生成器
    private Random                             rand                      = new Random();

    //是否在运行中
    private boolean                            isRuning                  = false;

    //Boss执行器名
    private String                             name;

    private ModuleJobConfig                    moduleJobConfig;

    private List<Job>                          jobListTemp               = Lists.newArrayList(); //待执行列表，开发或测试环境，任务新建时立即将其加入

    public List<Job> getJobListTemp() {
        return jobListTemp;
    }

    public void setJobListTemp(List<Job> jobListTemp) {
        this.jobListTemp = jobListTemp;
    }

    static {
        FAIL_JOB_DELAY_TIME_CFG.put(0, 0); //首次无延迟控制
        FAIL_JOB_DELAY_TIME_CFG.put(1, 1);
        FAIL_JOB_DELAY_TIME_CFG.put(2, 1);
        FAIL_JOB_DELAY_TIME_CFG.put(3, 1);
        FAIL_JOB_DELAY_TIME_CFG.put(4, 1);
        FAIL_JOB_DELAY_TIME_CFG.put(5, 1);
        FAIL_JOB_DELAY_TIME_CFG.put(6, 1);
        FAIL_JOB_DELAY_TIME_CFG.put(7, 1);
        FAIL_JOB_DELAY_TIME_CFG.put(8, 1);
    }

    public JobBoss(ModuleJobConfig moduleJobConfig) {
        this.moduleJobConfig = moduleJobConfig;
        this.name = "BOSS-" + moduleJobConfig.getModuleCode() + "@" + IPUtil.getLocalIP();
        if (StringUtils.isNotBlank(moduleJobConfig.getBossWokerCount())) {
            this.wokerCount = Integer.parseInt(moduleJobConfig.getBossWokerCount());
        }
        moduleJobConfig.setModuleJobBoss(this);
    }

    public String getName() {
        return name;
    }

    /**
     * 停止Boss执行器
     */
    public synchronized void stop() {
        if (!this.isRuning) {
            return;
        }
        this.isRuning = false;
        for (JobWorker worker : jobWorkers) {
            worker.stop();
        }
    }

    /**
     * 启动Boss执行器
     */
    public synchronized void start() {
        if (this.isRuning) {
            return;
        }
        log.info("JobBoss name={} begin start ... ", this.name);
        jobWorkers = new JobWorker[wokerCount];
        for (int i = 0; i < wokerCount; i++) {
            jobWorkers[i] = new JobWorker(moduleJobConfig, i);
            jobWorkers[i].start();
        }
        this.isRuning = true;
        new Thread(this).start(); //启动Boss线程
        log.info("JobBoss name={} start sucess", this.name);
    }

    @Override
    public void run() {
        int index = 0; //查询Job的起始位置
        List<Job> jobList = null;
        while (this.isRuning) {
            try {
                jobList = getJobService().listPendingJobByPage(moduleJobConfig.getModuleCode(), index,
                        EVERY_QUERY_JOB_MAX_COUNT);
                log.debug("list pending job, moduleCode={}, index={}, resultJobList={}",
                        moduleJobConfig.getModuleCode(), index, Job.jobListToString(jobList));
                if (CollectionUtils.isEmpty(jobList)) {
                    continue;
                }
                for (Job job : jobList) {
                    if (checkJob(job) && lockJob(job)) {
                        this.assignJobToWoker(job); //分派Job给Worker处理
                    }
                }
            } catch (Exception e) {
                log.warn("JobBoss查询并分配待处理Job异常：", e);
            } finally {
                //查询待处理任务起始位置index的处理
                if (index == 0) {
                    if (jobList == null || jobList.size() < EVERY_QUERY_JOB_MAX_COUNT) { //查询结果为0或不满最大值，则休眠60秒，index仍为0
                        try {
                            Thread.sleep(60000L);
                        } catch (Exception e) {
                            log.warn("JobBoss处理查询Index休眠被打断，异常：", e);
                        }
                    } else if (jobList.size() == EVERY_QUERY_JOB_MAX_COUNT) { //查询结果为最大值，则index为0-MaxCount之间的随机值
                        index = rand.nextInt(EVERY_QUERY_JOB_MAX_COUNT);
                    }
                } else {
                    if (jobList == null || jobList.size() == 0) {//查询结果为0，则index调整为0从头开始查
                        index = 0;
                    } else if (jobList.size() == EVERY_QUERY_JOB_MAX_COUNT) {//查询结果为最大值，则index为原值+随机量
                        index += rand.nextInt(EVERY_QUERY_JOB_MAX_COUNT);
                    } else { //查询结果为中间值，则index调整为0-index之间的随机值
                        index = rand.nextInt(index);
                    }
                }
            }
        }
    }

    /**
     * 检查Job是否可以被锁定处理
     */
    private boolean checkJob(Job job) {
        if (job == null || job.getJobStatus() == null || StringUtils.isBlank(job.getJobCode())) {
            return false;
        }
        long nowTime = System.currentTimeMillis();
        switch (job.getJobStatus()) {
            case FAIL:
                //执行8次以上或失败3天以上的Job直接忽略
                int execCount = job.getExecCount();
                if (execCount > 8 || DateUtil.getIntervalDay(job.getCreatedTime().getTime(), nowTime) > 3) {
                    log.debug(
                            "Job can't be executed, because job has been executed 8 times or job created at 3 days ago, job={}",
                            job.toString());
                    return false;
                }
                //执行失败的Job在延迟时间配置内无需再次执行
                if (DateUtil.getIntervalMinute(job.getExecTime().getTime(), nowTime) < FAIL_JOB_DELAY_TIME_CFG
                        .get(execCount)) {
                    log.debug(
                            "Job can't be executed, because job executed fail time distance now not achieve config time interval , job={}",
                            job.toString());
                    return false;
                }
                return true;
            case LOCK:
                //锁定的Job小于强制释放时间，则忽略
                JobConfig jobConfig = moduleJobConfig.getJobConfig(job.getJobCode());
                int lockReleaseMinutes = LOCK_JOB_RELEASE_TIME;
                if (StringUtils.isNotBlank(jobConfig.getLockTime())) {
                    lockReleaseMinutes = Integer.parseInt(jobConfig.getLockTime());
                }
                if (DateUtil.getIntervalMinute(job.getExecTime().getTime(), nowTime) < lockReleaseMinutes) {
                    log.debug(
                            "Job can't be executed, because job locked time distance now not achieve config time interval , job={}, lockReleaseMinutes={}",
                            job.toString(), lockReleaseMinutes);
                    return false;
                }
                return true;
            case CREATE:
                return true;
            default:
                return false;
        }
    }

    /**
     * 利用数据库机制抢占式锁定Job
     */
    private boolean lockJob(Job job) {
        int modifyCnt = getJobService().modifyJobStatusByIDandStatus(job.getId(), job.getJobStatus(), this.name,
                JobStatus.LOCK, job.getOperator());
        if (modifyCnt == 1) {
            job.setJobStatus(JobStatus.LOCK);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 循环获取下一个Woker执行器
     */
    private JobWorker nextWoker() {
        int index = wokerIndex.incrementAndGet() % wokerCount;
        return this.jobWorkers[index];
    }

    /**
     * 分派Job任务给Worker执行器
     */
    public void assignJobToWoker(Job job) {
        if (job == null) {
            return;
        }
        int tryCount = 1;
        while (this.isRuning) {
            JobWorker worker = this.nextWoker();
            if (worker.putJob(job)) {
                log.debug("JobBoss put job  to JobWorker, job={}, woker={}", job.toString(), worker.getName());
                break;
            } else {
                if (tryCount++ % wokerCount == 0) { //如果所有Worker执行器队列都是满的，则休息30秒后再分配
                    try {
                        Thread.sleep(30000L);
                    } catch (Exception e) {
                        log.warn("JobBoss分配Wroker任务休眠异常：", e);
                    }
                }
            }
        }
    }

    private JobService getJobService() {
        return ServiceManager.service(JobService.class);
    }

}
