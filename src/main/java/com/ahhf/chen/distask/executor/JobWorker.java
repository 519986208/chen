package com.ahhf.chen.distask.executor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import com.ahhf.chen.distask.domain.Job;
import com.ahhf.chen.distask.domain.JobContext;
import com.ahhf.chen.distask.domain.JobExecLog;
import com.ahhf.chen.distask.domain.JobResult;
import com.ahhf.chen.distask.domain.JobTask;
import com.ahhf.chen.distask.domain.JobTaskExecLog;
import com.ahhf.chen.distask.domain.TaskContext;
import com.ahhf.chen.distask.domain.config.JobConfig;
import com.ahhf.chen.distask.domain.config.JobTaskConfig;
import com.ahhf.chen.distask.domain.config.JobTaskConstant;
import com.ahhf.chen.distask.domain.config.ModuleJobConfig;
import com.ahhf.chen.distask.enums.ErrorCode;
import com.ahhf.chen.distask.enums.JobStatus;
import com.ahhf.chen.distask.enums.JobTaskStatus;
import com.ahhf.chen.distask.service.JobProcess;
import com.ahhf.chen.distask.service.JobService;
import com.ahhf.chen.distask.service.JobTaskProcess;
import com.ahhf.chen.distask.util.IPUtil;
import com.ahhf.chen.distask.util.ServiceManager;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JobWorker implements Runnable {

    //Woker执行队列最大数量
    private static final Integer MAX_WORKER_QUEUE_COUNT  = 20;

    //任务每次执行的分页数量
    private static final Integer MAX_TASK_EXEC_PAGE_SIZE = 50;

    //Task处理的线程池数量，默认为4
    private int                  threadPoolSize          = 4;

    //Woker可执行Job队列
    private BlockingQueue<Job>   jobQueue                = null;

    //JobTask处理时，如果处理数据量太大，则启动多线程处理
    private ExecutorService      taskExecutor            = null;

    //是否在运行中
    private boolean              isRuning                = false;

    //Woker处理器名
    private String               name;

    private ModuleJobConfig      moduleJobConfig;

    public JobWorker(ModuleJobConfig moduleJobConfig, int workIndex) {
        this.moduleJobConfig = moduleJobConfig;
        this.name = "WORKER-" + workIndex + "-" + moduleJobConfig.getModuleCode() + "@" + IPUtil.getLocalIP();
        if (moduleJobConfig.getWokerThreadSize() != null) {
            this.threadPoolSize = Integer.parseInt(moduleJobConfig.getWokerThreadSize());
        }
    }

    public String getName() {
        return name;
    }

    /**
     * 停止Worker执行器
     */
    public synchronized void stop() {
        if (!this.isRuning) {
            return;
        }
        this.isRuning = false;
        if (taskExecutor != null) {
            taskExecutor.shutdown();
        }
    }

    /**
     * 启动Worker执行器
     */
    public synchronized void start() {
        if (this.isRuning) {
            return;
        }
        log.info("JobWoker name={} begin start ... ", this.name);
        jobQueue = new LinkedBlockingQueue<>(MAX_WORKER_QUEUE_COUNT);
        taskExecutor = Executors.newFixedThreadPool(threadPoolSize);
        this.isRuning = true;
        new Thread(this).start(); //启动worker线程
        log.info("JobWoker name={} start success!", this.name);
    }

    /**
     * 给worker队列增加可执行Job，如果队列已满，有5秒的等待时间<br>
     * 添加成功返回true，添加失败返回false
     */
    public boolean putJob(Job job) {
        boolean isSuccess = false;
        try {
            isSuccess = jobQueue.offer(job, 5, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("JobWorker队列增加任务异常：", e);
        }
        return isSuccess;
    }

    @Override
    public void run() {
        while (this.isRuning) {
            JobContext jobContext = null;
            JobProcess jobProcess = null;
            JobResult jobResult = null;
            JobExecLog jobLog = null;
            try {
                Job job = jobQueue.take(); // 阻塞等待获取队列中的job
                if (job == null) {
                    continue;
                }
                log.debug("Job Worker handle job={}", job.toString());
                //校验Job，并初始化Job执行上下文
                jobContext = new JobContext(job);
                jobResult = checkAndInitJobContext(jobContext);
                log.debug("Job Worker init jobContext, job={}, result={}", job.toString(),
                        JSON.toJSONString(jobResult));
                if (!jobResult.isSuccess()) {
                    jobLog = buildJobExecLog(jobContext, jobResult);
                    getJobService().saveJobExecResult(jobLog);
                    continue;
                }
                //执行JobProcess的init方法
                jobProcess = jobContext.getJobProcess();
                jobProcess.init(jobContext.getJobConfig(), job);
                log.debug("Job Worker execute jobProcess init method, job={}", job.toString());
                //查询该Job的上一次的执行日志
                JobExecLog lastJobLog = getJobService().queryLatestLogByJobId(job.getId());
                //执行JobProcess的execute方法（如果Job上次已经执行成功，则不再执行）
                if (lastJobLog == null || !StringUtils.equals("true",
                        lastJobLog.getSummaryData(JobTaskConstant.JOB_LOG_KEY_EXEC_STATUS))) {
                    jobResult = jobProcess.execute(job);
                    log.debug("Job Worker execute jobProcess execute method, job={}, result={}", job.toString(),
                            JSON.toJSONString(jobResult));
                }
                //保存Job执行日志，如果JobProcess的execute方法返回false，则不再执行后续Task
                jobLog = buildJobExecLog(jobContext, jobResult);
                Long curJobLogId = getJobService().saveJobExecResult(jobLog);
                if (!jobResult.isSuccess()) {
                    continue;
                }
                jobLog.setId(curJobLogId);
                //处理Job下的所有task（注意：该方法不能抛出任何异常，否则一次执行会生成2条job日志）
                handleJobTasks(jobContext, lastJobLog != null ? lastJobLog.getId() : null, jobLog);
            } catch (Exception e) {
                log.error("JobWorker处理Job异常：", e);
                try {
                    if (jobContext != null) {
                        jobResult = new JobResult(e.getMessage(), getStackTrace(e));
                        jobLog = buildJobExecLog(jobContext, jobResult);
                        getJobService().saveJobExecResult(jobLog);
                    }
                } catch (Exception e1) {
                    log.error("JobWorker保存Job执行日志异常：", e1);
                }
            } finally {
                try {
                    //更新Job最终结果状态
                    if (jobContext != null) {
                        getJobService().modifyJobStatusByIDandStatus(jobContext.getJob().getId(),
                                jobContext.getJob().getJobStatus(), this.name, jobContext.getFinalJobStatus(),
                                this.name);
                        //如Job最终执行失败，则执行失败
                        if (jobContext.getFinalJobStatus() == JobStatus.FAIL) {
                            jobProcess.failNotice(jobContext.getJob(), jobContext.getFinalErrBuf().toString());
                        }
                    }
                    //执行JobProcess的destroy方法
                    if (jobProcess != null) {
                        jobProcess.destroy();
                    }
                } catch (Exception e) {
                    log.warn("JobWorker处理Job后执行通知和destroy方法异常:", e);
                }
            }
        }
    }

    private JobResult checkAndInitJobContext(JobContext jobContext) {
        JobResult jobResult = new JobResult();
        if (StringUtils.isBlank(jobContext.getJob().getJobCode())) {
            jobResult.setErrorMsg(ErrorCode.CHECK_JOBCODE_NOT_EMPTY.getErrorMsg());
            return jobResult;
        }
        JobConfig jobConfig = moduleJobConfig.getJobConfig(jobContext.getJob().getJobCode());
        if (jobConfig == null) {
            jobResult.setErrorMsg(ErrorCode.CHECK_JOBCONFIG_NOT_NULL.getErrorMsg());
            return jobResult;
        }
        jobContext.setJobConfig(jobConfig);
        String jobClassBeanName = jobConfig.getJobClass();
        if (StringUtils.isBlank(jobClassBeanName)) {
            jobClassBeanName = "DefaultJobProcess";
        }
        JobProcess jobProcess = this.getJobProcess(jobClassBeanName);
        if (jobProcess == null) {
            jobResult.setErrorMsg(ErrorCode.CHECK_JOBPROCESS_NOT_NULL.getErrorMsg());
            return jobResult;
        }
        jobContext.setJobProcess(jobProcess);
        jobResult.setSuccess(true);
        return jobResult;
    }

    /**
     * 处理Job下所有关联的Task任务
     * 
     * @param jobConfig
     * @param job
     * @param lastJobLog
     * @return
     */
    @SuppressWarnings("rawtypes")
    private void handleJobTasks(JobContext jobContext, final Long lastJobLogId, final JobExecLog curJobLog) {
        List<JobTask> jobTasks = null;
        try {
            jobTasks = getJobService().listJobTaskByJobId(jobContext.getJob().getId());
            log.debug("Job Worker handle JobTasks, query jobTaskList={}", JobTask.taskListToString(jobTasks));
            if (CollectionUtils.isEmpty(jobTasks)) {
                return;
            }
            jobContext.getJob().setJobTasks(jobTasks);
        } catch (Exception ex) {
            log.error("JobWorker处理JobTasks异常：", ex);
            try {
                curJobLog.setSummaryData(JobTaskConstant.JOB_LOG_KEY_TASKS_FAIL, "true");
                curJobLog.setErrMsg(ex.getMessage());
                curJobLog.setErrStack(getStackTrace(ex));
                getJobService().saveJobExecResult(curJobLog);
            } catch (Exception ex2) {
                log.error("JobWorker更新Job执行日志失败：", ex2);
            }
            return;
        }
        for (JobTask task : jobTasks) { //Job下的Task必须是按照ordering已排好序
            TaskContext taskContext = null;
            JobTaskProcess taskProcess = null;
            JobResult taskResult = null;
            JobTaskExecLog curTaskLog = null;
            try {
                if (task == null || task.getTaskStatus() == JobTaskStatus.SUCCESS) {
                    continue;
                }
                log.debug("Job Worker handle jobTask={}", task.toString());
                //校验Task，并初始化Task执行上下文
                taskContext = new TaskContext(jobContext, task);
                taskResult = checkAndInitTaskContext(taskContext);
                log.debug("Job Worker init taskContext, jobTask={}, result={}", task.toString(),
                        JSON.toJSONString(taskResult));
                if (!taskResult.isSuccess()) {
                    curTaskLog = buildTaskExecLog(taskContext, curJobLog.getId(), null, taskResult);
                    getJobService().saveJobTaskExecResult(curTaskLog);
                    continue;
                }
                //如有依赖顺序的高优先级Task执行失败,则后续Task全部失败
                if (jobContext.isDependTaskFail() && task.getOrdering() != null) {
                    taskResult = new JobResult(ErrorCode.CHECK_DEPEND_TASK_ERROR.getErrorMsg());
                    curTaskLog = buildTaskExecLog(taskContext, curJobLog.getId(), null, taskResult);
                    getJobService().saveJobTaskExecResult(curTaskLog);
                    continue;
                }
                //执行taskProcess的init方法
                taskProcess = taskContext.getTaskProcess();
                taskProcess.init(taskContext.getTaskConfig(), task);
                log.debug("Job Worker execute taskProcess init method, jobTask={}", task.toString());
                //处理task的实际业务
                int sumCount = taskProcess.count(task);
                int startPos = 0;
                if (sumCount == 0) {//不执行load方法，单个线程处理
                    executeTaskPageData(taskContext, -1, lastJobLogId, curJobLog.getId());
                } else if (sumCount < MAX_TASK_EXEC_PAGE_SIZE) {//执行load方法，单个线程处理
                    executeTaskPageData(taskContext, startPos, lastJobLogId, curJobLog.getId());
                } else {//多个线程处理
                    List<Future<JobResult>> taskFTList = Lists.newArrayList();
                    while (startPos < sumCount) {
                        taskFTList.add(taskExecutor.submit(
                                new TaskPageDataCallable(taskContext, startPos, lastJobLogId, curJobLog.getId())));
                        startPos += MAX_TASK_EXEC_PAGE_SIZE;
                    }
                    for (Future<JobResult> pageResFuture : taskFTList) {
                        taskResult = pageResFuture.get(); //等待线程处理完
                    }
                }
            } catch (Exception e) {
                log.error("JobWorker处理Task异常：", e);
                try {
                    if (taskContext != null) {
                        taskResult = new JobResult(e.getMessage(), getStackTrace(e));
                        curTaskLog = buildTaskExecLog(taskContext, curJobLog.getId(), null, taskResult);
                        getJobService().saveJobTaskExecResult(curTaskLog);
                    }
                } catch (Exception e1) {
                    log.error("JobWorker保存Task执行日志异常：", e1);
                }
            } finally {
                try {
                    //更新Task最终结果状态
                    if (taskContext != null) {
                        getJobService().modifyTaskStatusByID(task.getId(), taskContext.getFinalTaskStatus(), this.name);
                    }
                    //执行taskProcess的destroy方法
                    if (taskProcess != null) {
                        taskProcess.destroy();
                    }
                } catch (Exception e) {
                    log.error("JobWorker处理Task后更新Task状态并执行destroy方法异常:", e);
                }
            }
        }
    }

    /**
     * 处理Task每一页的数据业务
     * 
     * @param taskContext
     * @param startPos，如值为-1，表示不需要执行load方法
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private JobResult executeTaskPageData(TaskContext taskContext, int startPos, Long lastJobLogId, Long curJobLogId) {
        JobResult taskPageResult = new JobResult(true);
        JobTaskProcess taskProcess = taskContext.getTaskProcess();
        JobTask task = taskContext.getTask();
        int pagePos = 0;
        boolean isNeedExecute = true;
        try {
            if (lastJobLogId != null) {
                JobTaskExecLog lastTaskLog = getJobService().queryTaskPageLogByStartPos(lastJobLogId, task.getId(),
                        startPos);
                if (lastTaskLog != null) {
                    if (StringUtils.equals("true",
                            lastTaskLog.getSummaryData(JobTaskConstant.TASK_LOG_KEY_EXEC_STATUS))) {
                        isNeedExecute = false;
                    } else {
                        String pagePosStr = lastTaskLog.getSummaryData(JobTaskConstant.TASK_LOG_KEY_PAGE_SUCCESS_COUNT);
                        if (StringUtils.isNotBlank(pagePosStr)) {
                            pagePos = Integer.parseInt(pagePosStr);
                        }
                    }
                }
            }
            if (isNeedExecute) {
                if (startPos == -1) {
                    taskPageResult = taskProcess.execute(task, null);
                    log.debug("Job Worker execute taskProcess execute method, task={}, result={}", task.toString(),
                            JSON.toJSONString(taskPageResult));
                } else {
                    List loadDatas = taskProcess.load(task, startPos, MAX_TASK_EXEC_PAGE_SIZE);
                    if (CollectionUtils.isEmpty(loadDatas)) {
                        log.error("JobWorker执行taskProcess的load方法返回为空，task={}, startPos={}", task.toString(), startPos);
                        taskPageResult = new JobResult("load方法的查询结果为空");
                    } else {
                        for (; pagePos < loadDatas.size(); pagePos++) {
                            Object argParam = loadDatas.get(pagePos);
                            taskPageResult = taskProcess.execute(task, argParam);
                            log.debug(
                                    "Job Worker execute taskProcess execute method, task={}, startPos={}, argParam={}, result={}",
                                    task.toString(), startPos, JSON.toJSONString(argParam),
                                    JSON.toJSONString(taskPageResult));
                            if (!taskPageResult.isSuccess()) {
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("JobWorker处理taskProcess的execute方法异常：", e);
            taskPageResult = new JobResult(e.getMessage(), getStackTrace(e));
        }
        try {
            JobTaskExecLog curTaskLog = buildTaskExecLog(taskContext, curJobLogId, startPos, taskPageResult);
            if (!taskPageResult.isSuccess()) {
                curTaskLog.setSummaryData(JobTaskConstant.TASK_LOG_KEY_PAGE_SUCCESS_COUNT, String.valueOf(pagePos));
            }
            getJobService().saveJobTaskExecResult(curTaskLog);
        } catch (Exception e) {
            log.error("JobWorker保存TaskPage执行日志异常：", e);
        }
        return taskPageResult;
    }

    /**
     * 组装Job的执行日志
     * 
     * @param job
     * @param jobResult
     */
    private JobExecLog buildJobExecLog(JobContext jobContext, JobResult jobResult) {
        Job job = jobContext.getJob();
        JobExecLog jobLog = new JobExecLog();
        jobLog.setJobId(job.getId());
        jobLog.setExecTime(new Date());
        jobLog.setOperator(job.getOperator());
        jobLog.setWokerName(this.name);
        if (jobResult.isSuccess()) {
            jobLog.setSummaryData(JobTaskConstant.JOB_LOG_KEY_EXEC_STATUS, "true");
        } else {
            jobLog.setSummaryData(JobTaskConstant.JOB_LOG_KEY_EXEC_STATUS, "false");
            jobLog.setErrMsg(jobResult.getErrorMsg());
            jobLog.setErrStack(jobResult.getErrStack());
            if (jobContext.getFinalJobStatus() == JobStatus.SUCCESS) {
                jobContext.setFinalJobStatus(JobStatus.FAIL);
            }
            if (StringUtils.isNotBlank(jobLog.getErrMsg())) {
                jobContext.appendError(jobLog.getErrMsg());
            }
            String strErrStack = jobLog.getErrStack();
            if (StringUtils.isNotBlank(strErrStack)) {
                jobContext.appendError(strErrStack.length() > 1000 ? strErrStack.substring(0, 1000) : strErrStack);
            }
        }
        return jobLog;
    }

    /**
     * 组装JobTask的执行日志
     * 
     * @param taskContext
     * @param curJobLogId
     * @param startPos
     * @param taskResult
     * @return
     */
    private JobTaskExecLog buildTaskExecLog(TaskContext taskContext, Long curJobLogId, Integer startPos,
                                            JobResult taskResult) {
        JobTask task = taskContext.getTask();
        JobExecLog jobLog = new JobExecLog();
        jobLog.setId(curJobLogId);
        jobLog.setJobId(task.getJobId());
        JobTaskExecLog taskLog = new JobTaskExecLog();
        taskLog.setJobLog(jobLog);
        taskLog.setJobTaskId(task.getId());
        taskLog.setStartPos(startPos);
        taskLog.setExecTime(new Date());
        taskLog.setOperator(this.name);
        if (taskResult.isSuccess()) {
            taskLog.setSummaryData(JobTaskConstant.TASK_LOG_KEY_EXEC_STATUS, "true");
        } else {
            taskLog.setSummaryData(JobTaskConstant.TASK_LOG_KEY_EXEC_STATUS, "false");
            taskLog.setErrMsg(taskResult.getErrorMsg());
            taskLog.setErrStack(taskResult.getErrStack());
            if (taskContext.getFinalTaskStatus() == JobTaskStatus.SUCCESS) {
                taskContext.setFinalTaskStatus(JobTaskStatus.FAIL);
            }
            if (taskContext.getFinalJobStatus() == JobStatus.SUCCESS) {
                taskContext.getJobContext().setFinalJobStatus(JobStatus.FAIL);
            }
            if (StringUtils.isNotBlank(taskLog.getErrMsg())) {
                taskContext.appendError(taskLog.getErrMsg());
            }
            String strErrStack = taskLog.getErrStack();
            if (StringUtils.isNotBlank(strErrStack)) {
                taskContext.appendError(strErrStack.length() > 1000 ? strErrStack.substring(0, 1000) : strErrStack);
            }
            if (task.getOrdering() != null && !taskContext.getDependTaskFail()) {
                taskContext.setDependTaskFail(true);
            }
        }
        return taskLog;
    }

    @SuppressWarnings("rawtypes")
    private JobResult checkAndInitTaskContext(TaskContext taskContext) {
        JobResult taskResult = new JobResult();
        JobTask task = taskContext.getTask();
        if (StringUtils.isBlank(task.getTaskCode())) {
            taskResult.setErrorMsg(ErrorCode.CHECK_TASKCODE_NOT_EMPTY.getErrorMsg());
            return taskResult;
        }
        JobTaskConfig taskConfig = taskContext.getJobConfig().getJobTaskConfig(task.getTaskCode());
        if (taskConfig == null || StringUtils.isBlank(taskConfig.getTaskClass())) {
            taskResult.setErrorMsg(ErrorCode.CHECK_TASKCONFIG_NOT_NULL.getErrorMsg());
            return taskResult;
        }
        taskContext.setTaskConfig(taskConfig);
        JobTaskProcess taskProcess = this.getTaskProcess(taskConfig.getTaskClass());
        if (taskProcess == null) {
            taskResult.setErrorMsg(ErrorCode.CHECK_TASKPROCESS_NOT_NULL.getErrorMsg());
            return taskResult;
        }
        taskContext.setTaskProcess(taskProcess);
        taskResult.setSuccess(true);
        return taskResult;
    }

    private String getStackTrace(Throwable ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        try {
            ex.printStackTrace(pw);
            return sw.toString();
        } finally {
            pw.close();
        }
    }

    class TaskPageDataCallable implements Callable<JobResult> {

        private TaskContext taskContext  = null;
        private int         startPos;
        private Long        lastJobLogId = null;
        private Long        curJobLogId  = null;

        public TaskPageDataCallable(TaskContext taskContext, int startPos, Long lastJobLogId, Long curJobLogId) {
            super();
            this.taskContext = taskContext;
            this.startPos = startPos;
            this.lastJobLogId = lastJobLogId;
            this.curJobLogId = curJobLogId;
        }

        @Override
        public JobResult call() throws Exception {
            JobResult pageTaskRes = executeTaskPageData(taskContext, startPos, lastJobLogId, curJobLogId);
            return pageTaskRes;
        }

    }

    private JobService getJobService() {
        return ServiceManager.service(JobService.class);
    }

    private JobProcess getJobProcess(String beanName) {
        return ServiceManager.bean(JobProcess.class, beanName);
    }

    @SuppressWarnings("rawtypes")
    private JobTaskProcess getTaskProcess(String beanName) {
        return ServiceManager.bean(JobTaskProcess.class, beanName);
    }

}
