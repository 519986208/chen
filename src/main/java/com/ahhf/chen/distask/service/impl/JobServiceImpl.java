package com.ahhf.chen.distask.service.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.ahhf.chen.datasource.sequence.Sequence;
import com.ahhf.chen.datasource.transaction.TransactionProcesser;
import com.ahhf.chen.datasource.transaction.TransactionService;
import com.ahhf.chen.distask.dao.QueueJobDAO;
import com.ahhf.chen.distask.dao.QueueJobExecLogDAO;
import com.ahhf.chen.distask.dao.QueueJobTaskDAO;
import com.ahhf.chen.distask.dao.QueueJobTaskExecLogDAO;
import com.ahhf.chen.distask.dao.dataobject.QueueJobDO;
import com.ahhf.chen.distask.dao.dataobject.QueueJobExecLogDO;
import com.ahhf.chen.distask.dao.dataobject.QueueJobTaskDO;
import com.ahhf.chen.distask.dao.dataobject.QueueJobTaskExecLogDO;
import com.ahhf.chen.distask.domain.Job;
import com.ahhf.chen.distask.domain.JobExecLog;
import com.ahhf.chen.distask.domain.JobTask;
import com.ahhf.chen.distask.domain.JobTaskExecLog;
import com.ahhf.chen.distask.enums.ErrorCode;
import com.ahhf.chen.distask.enums.JobStatus;
import com.ahhf.chen.distask.enums.JobTaskStatus;
import com.ahhf.chen.distask.enums.SaveType;
import com.ahhf.chen.distask.exception.BusinessException;
import com.ahhf.chen.distask.exception.ExceptionUtils;
import com.ahhf.chen.distask.service.JobService;
import com.ahhf.chen.distask.util.JobSaveAssistant;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class JobServiceImpl implements JobService {

    @Autowired
    private QueueJobDAO            queueJobDAO;
    @Autowired
    private QueueJobTaskDAO        queueJobTaskDAO;
    @Autowired
    private QueueJobExecLogDAO     queueJobExecLogDAO;
    @Autowired
    private QueueJobTaskExecLogDAO queueJobTaskExecLogDAO;
    @Autowired
    private Sequence               seqQueueJob;
    @Autowired
    private Sequence               seqQueueJobTask;
    @Autowired
    private Sequence               seqQueueJobExecLog;
    @Autowired
    private Sequence               seqQueueJobTaskExecLog;

    @Autowired
    private TransactionService     combinedService;

    public JobServiceImpl() {
        super();
    }

    @Override
    public List<Job> listPendingJobByPage(String moduleCode, Integer start, Integer limit) {
        if (StringUtils.isBlank(moduleCode) || start == null || limit == null) {
            throw ExceptionUtils.throwException(ErrorCode.CMN_PARAMETER_ERROR, "moduleCode, start, limit 不能为空");
        }
        List<Job> jobList = null;
        try {
            List<QueueJobDO> jobDOList = queueJobDAO.selectPendingJobByPage(moduleCode, start, limit);
            if (!CollectionUtils.isEmpty(jobDOList)) {
                jobList = Lists.newArrayList();
                for (QueueJobDO jobDO : jobDOList) {
                    jobList.add(new Job(jobDO));
                }
            }
        } catch (Exception e) {
            log.error("查询待处理Job列表出错，moduleCode={}，start={}，limit={}, Exception:", moduleCode, start, limit, e);
            throw new BusinessException(e.getMessage());
        }
        return jobList;
    }

    @Override
    public int modifyJobStatusByIDandStatus(Long jobId, JobStatus oldStatus, String modifier, JobStatus newStatus,
                                            String oldModifier) {
        if (jobId == null || oldStatus == null || newStatus == null || StringUtils.isBlank(modifier)) {
            throw ExceptionUtils.throwException(ErrorCode.CMN_PARAMETER_ERROR,
                    "jobId, oldStatus, modifier, newStatus 不能为空");
        }
        try {
            return (int) combinedService.doInTransaction(new TransactionProcesser() {

                @Override
                public Object execute(Object... params) throws Exception {
                    return queueJobDAO.updateJobStatusByID((Long) params[0], (String) params[1], (String) params[2],
                            (String) params[3], (String) params[4]);
                }

            }, jobId, oldStatus.getCode(), modifier, newStatus.getCode(), oldModifier);

        } catch (Exception e) {
            log.error("更新Job状态出错，jobId={}，oldStatus={}，modifier={}，newStatus={}，Exception:", jobId, oldStatus, modifier,
                    newStatus, e);
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public int modifyTaskStatusByID(Long taskId, JobTaskStatus taskStatus, String modifier) {
        if (taskId == null || taskStatus == null || StringUtils.isBlank(modifier)) {
            throw ExceptionUtils.throwException(ErrorCode.CMN_PARAMETER_ERROR, "taskId, taskStatus, modifier 不能为空");
        }
        try {
            return (int) combinedService.doInTransaction(new TransactionProcesser() {

                @Override
                public Object execute(Object... params) throws Exception {
                    return queueJobTaskDAO.updateTaskStatusByID((Long) params[0], (String) params[1],
                            (String) params[2]);
                }
            }, taskId, taskStatus.getCode(), modifier);

        } catch (Exception e) {
            log.error("更新Task状态出错，taskId={}，taskStatus={}，modifier={}，Exception:", taskId, taskStatus, modifier, e);
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public Long saveJobExecResult(JobExecLog jobLog) {
        if (jobLog == null) {
            throw ExceptionUtils.throwException(ErrorCode.CMN_PARAMETER_ERROR, "jobLog 不能为空");
        }
        log.info("JobService saveJobExecResult, param jobLog={}", JSON.toJSONString(jobLog));
        try {
            QueueJobExecLogDO jobLogDO = jobLog.convertToDO();
            return (Long) combinedService.doInTransaction(new TransactionProcesser() {

                @Override
                public Object execute(Object... params) throws Exception {
                    QueueJobExecLogDO innerJobLogDO = (QueueJobExecLogDO) params[0];
                    Long jogLogId = innerJobLogDO.getId();
                    if (jogLogId == null) {
                        jogLogId = seqQueueJobExecLog.nextValue();
                        innerJobLogDO.setId(jogLogId);
                        queueJobExecLogDAO.insert(innerJobLogDO);
                        queueJobDAO.updateJobExecCountByID(innerJobLogDO.getJobId(), innerJobLogDO.getModifier());
                    } else {
                        queueJobExecLogDAO.updateByPrimaryKeySelective(innerJobLogDO);
                    }
                    log.info("JobService saveJobExecResult, param jobId={}, result jobLogId={}",
                            innerJobLogDO.getJobId(), jogLogId);
                    return jogLogId;
                }
            }, jobLogDO);

        } catch (Exception e) {
            log.error("保存Job执行Log出错，jobLog={}，Exception：", JSON.toJSONString(jobLog), e);
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public Long saveJobTaskExecResult(JobTaskExecLog jobTaskLog) {
        if (jobTaskLog == null) {
            throw ExceptionUtils.throwException(ErrorCode.CMN_PARAMETER_ERROR, "jobTaskLog 不能为空");
        }
        log.info("JobService saveJobTaskExecResult, param jobTaskLog={}", JSON.toJSONString(jobTaskLog));
        try {
            QueueJobTaskExecLogDO jobTaskLogDO = jobTaskLog.convertToDO();
            return (Long) combinedService.doInTransaction(new TransactionProcesser() {

                @Override
                public Object execute(Object... params) throws Exception {
                    QueueJobTaskExecLogDO innerTaskLogDO = (QueueJobTaskExecLogDO) params[0];
                    Long taskLogId = seqQueueJobTaskExecLog.nextValue();
                    innerTaskLogDO.setId(taskLogId);
                    queueJobTaskExecLogDAO.insert(innerTaskLogDO);
                    log.info("JobService saveJobTaskExecResult, param jobTaskId={}, result taskLogId={}",
                            innerTaskLogDO.getJobTaskId(), taskLogId);
                    return taskLogId;
                }
            }, jobTaskLogDO);

        } catch (Exception e) {
            log.error("保存JobTask执行Log出错，jobTaskLog={}，Exception：", JSON.toJSONString(jobTaskLog), e);
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public JobExecLog queryLatestLogByJobId(Long jobId) {
        if (jobId == null) {
            throw ExceptionUtils.throwException(ErrorCode.CMN_PARAMETER_ERROR, "jobId 不能为空");
        }
        JobExecLog jobLog = null;
        try {
            QueueJobExecLogDO jobLogDO = queueJobExecLogDAO.selectLatestLogByJobId(jobId);
            if (jobLogDO != null) {
                jobLog = new JobExecLog(jobLogDO);
                log.info("JobService queryLatestLogByJobId, param jobId={}, result jobLog={}", jobId,
                        JSON.toJSONString(jobLog));
            }
        } catch (Exception e) {
            log.error("查询Job最近一次执行Log出错，jobId={}，Exception：", jobId, e);
            throw new BusinessException(e.getMessage());
        }
        return jobLog;
    }

    @Override
    public List<JobTask> listJobTaskByJobId(Long jobId) {
        if (jobId == null) {
            throw ExceptionUtils.throwException(ErrorCode.CMN_PARAMETER_ERROR, "jobId 不能为空");
        }
        List<JobTask> taskList = null;
        try {
            List<QueueJobTaskDO> taskDOList = queueJobTaskDAO.selectTasksByJobId(jobId);
            if (!CollectionUtils.isEmpty(taskDOList)) {
                taskList = Lists.newArrayList();
                for (QueueJobTaskDO taskDO : taskDOList) {
                    taskList.add(new JobTask(taskDO));
                }
                Collections.sort(taskList, new Comparator<JobTask>() {

                    @Override
                    public int compare(JobTask task1, JobTask task2) {
                        if (task1.getOrdering() == null && task2.getOrdering() == null) {
                            return 0;
                        }
                        if (task1.getOrdering() == null) {
                            return 1;
                        }
                        if (task2.getOrdering() == null) {
                            return -1;
                        }
                        return task1.getOrdering().compareTo(task2.getOrdering());
                    }
                });
            }
        } catch (Exception e) {
            log.error("查询Job下Task列表出错，jobId={}，Exception：", jobId, e);
            throw new BusinessException(e.getMessage());
        }
        return taskList;
    }

    @Override
    public JobTaskExecLog queryTaskPageLogByStartPos(Long jobLogId, Long taskId, int startPos) {
        if (jobLogId == null || taskId == null) {
            throw ExceptionUtils.throwException(ErrorCode.CMN_PARAMETER_ERROR, "jobLogId和taskId 不能为空");
        }
        JobTaskExecLog taskLog = null;
        try {
            QueueJobTaskExecLogDO taskLogDO = queueJobTaskExecLogDAO.selectTaskPageLogByTaskIdAndStartPos(jobLogId,
                    taskId, Integer.valueOf(startPos));
            if (taskLogDO != null) {
                taskLog = new JobTaskExecLog(taskLogDO);
                log.info(
                        "JobService queryTaskPageLogByStartPos, param jobLogId={}, taskId={}, startPos={}, result jobLog={}",
                        jobLogId, taskId, startPos, JSON.toJSONString(taskLog));
            }
        } catch (Exception e) {
            log.error("查询Task分页数据最近一次执行Log出错，jobLogId={}，taskId={}，startPos={}，Exception:", jobLogId, taskId, startPos,
                    e);
            throw new BusinessException(e.getMessage());
        }
        return taskLog;
    }

    @Override
    public Long saveAndModifyJob(Job newJob) {
        if (newJob == null) {
            throw ExceptionUtils.throwException(ErrorCode.CMN_PARAMETER_ERROR, "job不能为null");
        }
        log.info("JobService saveAndModifyJob, param newJob={}", JSON.toJSONString(newJob));
        Long jobId = null;
        try {
            Job oldJob = null;
            if (newJob.getId() != null && newJob.getId() != 0L) {
                oldJob = this.queryJobById(newJob.getId());
                if (oldJob == null) {
                    throw ExceptionUtils.throwException(ErrorCode.SAVE_JOB_ERROR,
                            "JobId=" + newJob.getId() + "关联的Job不存在");
                }
            } else {
                oldJob = this.queryJobByBusinessNo(newJob.getModuleCode(), newJob.getJobCode(), newJob.getBusinessNo());
            }
            QueueJobDO jobDO = null;
            SaveType saveType = JobSaveAssistant.getChangedType(newJob, oldJob);
            switch (saveType) {
                case ADD:
                    jobDO = newJob.convertToDO();
                    break;
                case UPDATE:
                    jobDO = newJob.convertToDO();
                    jobDO.setId(oldJob.getId());
                    break;
                case DELETE:
                    jobDO = new QueueJobDO();
                    jobDO.setId(newJob.getId());
                    jobDO.setIsDeleted("Y");
                    jobDO.setModifier(newJob.getOperator());
                    break;
                default:
                    break;
            }
            List<QueueJobTaskDO> taskDOList = generateNeedSaveTasks(newJob, oldJob, saveType);
            jobId = handleSaveJobData(jobDO, taskDOList);
            if (jobId == null) {
                jobId = newJob.getId();
            }
        } catch (Exception e) {
            log.error("保存Job出错，newJob={}，Exception：", JSON.toJSONString(newJob), e);
            throw new BusinessException(e.getMessage());
        }
        return jobId;
    }

    private Long handleSaveJobData(QueueJobDO jobDO, List<QueueJobTaskDO> taskDOList) throws Exception {
        return (Long) combinedService.doInTransaction(new TransactionProcesser() {
            @SuppressWarnings("unchecked")
            @Override
            public Object execute(Object... params) throws Exception {
                Long jobId = null;
                QueueJobDO InnerjobDO = (QueueJobDO) params[0];
                if (InnerjobDO != null) {
                    jobId = InnerjobDO.getId();
                    if (jobId == null || jobId == 0L) {
                        jobId = seqQueueJob.nextValue();
                        InnerjobDO.setId(jobId);
                        queueJobDAO.insert(InnerjobDO);
                    } else {
                        queueJobDAO.updateByPrimaryKeySelective(InnerjobDO);
                    }
                }
                List<QueueJobTaskDO> innerTaskDOList = (List<QueueJobTaskDO>) params[1];
                if (!CollectionUtils.isEmpty(innerTaskDOList)) {
                    for (QueueJobTaskDO taskDO : innerTaskDOList) {
                        if (taskDO.getId() == null || taskDO.getId() == 0L) {
                            taskDO.setId(seqQueueJobTask.nextValue());
                            taskDO.setJobId(jobId);
                            queueJobTaskDAO.insert(taskDO);
                        } else {
                            queueJobTaskDAO.updateByPrimaryKeySelective(taskDO);
                        }
                    }
                }
                return jobId;
            }
        }, jobDO, taskDOList);
    }

    private List<QueueJobTaskDO> generateNeedSaveTasks(Job newJob, Job oldJob, SaveType jobSaveType) {
        if (newJob.getJobTasks() == null) {//如果为null表示不需要更新task
            return null;
        }
        List<QueueJobTaskDO> taskDOList = Lists.newArrayList();
        QueueJobTaskDO taskDO = null;
        switch (jobSaveType) {
            case ADD:
                if (!CollectionUtils.isEmpty(newJob.getJobTasks())) {
                    for (JobTask newTask : newJob.getJobTasks()) {
                        taskDOList.add(newTask.convertToDO());
                    }
                }
                break;
            case DELETE:
                if (!CollectionUtils.isEmpty(oldJob.getJobTasks())) {
                    for (JobTask oldTask : oldJob.getJobTasks()) {
                        taskDO = new QueueJobTaskDO();
                        taskDO.setId(oldTask.getId());
                        taskDO.setIsDeleted("Y");
                        taskDO.setModifier(newJob.getOperator());
                        taskDOList.add(taskDO);
                    }
                }
                break;
            default:
                JobTask tempTask = null;
                if (!CollectionUtils.isEmpty(newJob.getJobTasks())) {
                    for (JobTask newTask : newJob.getJobTasks()) {
                        tempTask = oldJob.getTaskByCode(newTask.getTaskCode());
                        switch (JobSaveAssistant.getChangedType(newTask, tempTask)) {
                            case ADD:
                                taskDOList.add(newTask.convertToDO());
                                break;
                            case UPDATE:
                                taskDO = newTask.convertToDO();
                                taskDO.setId(tempTask.getId());
                                taskDOList.add(taskDO);
                                break;
                            case DELETE:
                                taskDO = new QueueJobTaskDO();
                                taskDO.setId(tempTask.getId());
                                taskDO.setIsDeleted("Y");
                                taskDO.setModifier(newJob.getOperator());
                                taskDOList.add(taskDO);
                                break;
                            default:
                                break;
                        }
                    }
                }
                if (oldJob != null && !CollectionUtils.isEmpty(oldJob.getJobTasks())) {
                    for (JobTask oldTask : oldJob.getJobTasks()) {
                        tempTask = newJob.getTaskByCode(oldTask.getTaskCode());
                        if (tempTask == null) {
                            taskDO = new QueueJobTaskDO();
                            taskDO.setId(oldTask.getId());
                            taskDO.setIsDeleted("Y");
                            taskDO.setModifier(newJob.getOperator());
                            taskDOList.add(taskDO);
                        }
                    }
                }
                break;
        }
        return taskDOList;
    }

    @Override
    public Job queryJobById(Long jobId) {
        if (jobId == null) {
            throw ExceptionUtils.throwException(ErrorCode.CMN_PARAMETER_ERROR, "jobId 不能为空");
        }
        Job job = null;
        List<JobTask> taskList = null;
        try {
            QueueJobDO jobDO = queueJobDAO.selectByPrimaryKey(jobId);
            if (jobDO != null) {
                job = new Job(jobDO);
                taskList = this.listJobTaskByJobId(jobId);
                job.setJobTasks(taskList);
            }
        } catch (Exception e) {
            log.error("根据Id查询Job出错，jobId={}，Exception：", jobId, e);
            throw new BusinessException(e.getMessage());
        }
        return job;
    }

    @Override
    public Job queryJobByBusinessNo(String moduleCode, String jobCode, String businessNo) {
        if (StringUtils.isBlank(moduleCode) || StringUtils.isBlank(jobCode) || StringUtils.isBlank(businessNo)) {
            throw ExceptionUtils.throwException(ErrorCode.CMN_PARAMETER_ERROR, "moduleCode,jobCode,businessNo 不能为空");
        }
        Job job = null;
        List<JobTask> taskList = null;
        try {
            QueueJobDO jobDO = queueJobDAO.selectJobByBusinessNo(moduleCode, businessNo, jobCode);
            if (jobDO != null) {
                job = new Job(jobDO);
                taskList = this.listJobTaskByJobId(job.getId());
                job.setJobTasks(taskList);
            }
        } catch (Exception e) {
            log.error("根据业务号查询Job出错，moduleCode={}，jobCode={}，businessNo={}，Exception：", moduleCode, jobCode, businessNo,
                    e);
            throw new BusinessException(e.getMessage());
        }
        return job;
    }
}
