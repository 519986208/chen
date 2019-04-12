package com.ahhf.chen.distask.service.impl;

import java.util.List;

import com.ahhf.chen.distask.domain.JobTask;
import com.ahhf.chen.distask.domain.config.JobTaskConfig;
import com.ahhf.chen.distask.service.JobTaskProcess;

public abstract class AbstractJobTaskProcess<T> implements JobTaskProcess<T> {

    /**
     * 默认返回数据量为0
     */
    private static final int DEFAULT_DATA_COUNT = 0;

    private JobTaskConfig    taskConfig;

    @Override
    public void init(JobTaskConfig taskConfig, JobTask task) {
        this.taskConfig = taskConfig;
        init(task);
    }

    @Override
    public JobTaskConfig getTaskConfig() {
        return this.taskConfig;
    }

    /**
     * 默认返回1条待处理的数据，如果有多条数据需要处理，需重写此方法
     */
    @Override
    public int count(JobTask task) {
        return DEFAULT_DATA_COUNT;
    }

    @Override
    public List<T> load(JobTask task, int start, int limit) {
        return null;
    }

    @Override
    public void destroy() {
        // empty code
    }

    /**
     * 用户自定义初始化方法，可以将该任务共用的参数初始化在jobCache中
     */
    public void init(JobTask task) {
        // empty code
    }

    /**
     * 获取TaskProcess对应的TaskCode
     */
    public String getJobTaskCode() {
        return this.taskConfig.getTaskCode();
    }

    /**
     * 获取taskConfig配置的初始化参数值
     */
    public String getInitParam(String paramName) {
        return this.taskConfig.getInitParameter(paramName);
    }

}
