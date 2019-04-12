package com.ahhf.chen.distask.service;

import java.util.List;

import com.ahhf.chen.distask.domain.Job;
import com.ahhf.chen.distask.domain.JobExecLog;
import com.ahhf.chen.distask.domain.JobTask;
import com.ahhf.chen.distask.domain.JobTaskExecLog;
import com.ahhf.chen.distask.enums.JobStatus;
import com.ahhf.chen.distask.enums.JobTaskStatus;

/**
 * 类JobService.java的实现描述：作业服务接口
 */
public interface JobService {

    /**
     * 查询对应模块待处理作业列表
     */
    List<Job> listPendingJobByPage(String moduleCode, Integer start, Integer limit);

    /**
     * 根据ID和状态更新，用数据库机制抢占式锁定任务
     */
    int modifyJobStatusByIDandStatus(Long jobId, JobStatus oldStatus, String modifier, JobStatus newStatus,
                                     String oldModifier);

    /**
     * 根据ID更新Task的状态
     */
    int modifyTaskStatusByID(Long taskId, JobTaskStatus taskStatus, String modifier);

    /**
     * 保存Job执行结果（保存执行日志和Job执行次数）<br>
     */
    Long saveJobExecResult(JobExecLog jobLog);

    /**
     * 保存JobTask的执行结果（仅保存执行日志，不更新task状态）
     */
    Long saveJobTaskExecResult(JobTaskExecLog curTaskLog);

    /**
     * 查询Job举例当前时间最近的一次执行日志
     */
    JobExecLog queryLatestLogByJobId(Long jobId);

    /**
     * 查询Job下所有的Task（如有task下有依赖顺序，需按照优先级高低排列）
     */
    List<JobTask> listJobTaskByJobId(Long jobId);

    /**
     * 根据TaskId和startPos查询Task对应分页数据的执行日志
     */
    JobTaskExecLog queryTaskPageLogByStartPos(Long jobLogId, Long taskId, int startPos);

    /**
     * 保存和修改Job(包括修改Job下的task）
     */
    Long saveAndModifyJob(Job job);

    /**
     * 根据JobId查询Job
     */
    Job queryJobById(Long jobId);

    /**
     * 根据业务号查询指定的Job
     */
    Job queryJobByBusinessNo(String moduleCode, String jobCode, String businessNo);

}
