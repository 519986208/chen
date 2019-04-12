package com.ahhf.chen.distask.domain;

import com.ahhf.chen.distask.domain.config.JobConfig;
import com.ahhf.chen.distask.domain.config.JobTaskConfig;
import com.ahhf.chen.distask.enums.JobStatus;
import com.ahhf.chen.distask.enums.JobTaskStatus;
import com.ahhf.chen.distask.service.JobTaskProcess;

import lombok.Data;

/**
 * 类TaskContext.java的实现描述：单个Task执行的上下文
 */
@Data
public class TaskContext {

    private JobContext     jobContext;

    private JobTask        task;

    /**
     * Task的配置信息
     */
    private JobTaskConfig  taskConfig;

    /**
     * Task处理器
     */
    @SuppressWarnings("rawtypes")
    private JobTaskProcess taskProcess;

    /**
     * Task的最终状态
     */
    private JobTaskStatus  finalTaskStatus = JobTaskStatus.SUCCESS;

    public TaskContext() {
        super();
    }

    public TaskContext(JobContext jobContext, JobTask task) {
        super();
        this.jobContext = jobContext;
        this.task = task;
    }

    public Job getJob() {
        return this.jobContext.getJob();
    }

    public JobConfig getJobConfig() {
        return this.jobContext.getJobConfig();
    }

    public JobStatus getFinalJobStatus() {
        return this.jobContext.getFinalJobStatus();
    }

    /**
     * 追加Job执行错误日志
     */
    public void appendError(String errMsg) {
        this.jobContext.appendError(errMsg);
    }

    /**
     * 设置依赖task失败标识
     */
    public void setDependTaskFail(boolean dependTaskFail) {
        this.jobContext.setDependTaskFail(dependTaskFail);
    }

    public boolean getDependTaskFail() {
        return this.jobContext.isDependTaskFail();
    }
}
