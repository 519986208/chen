package com.ahhf.chen.distask.service.impl;

import com.ahhf.chen.distask.domain.Job;
import com.ahhf.chen.distask.domain.config.JobConfig;
import com.ahhf.chen.distask.service.JobProcess;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractJobProcess implements JobProcess {

    private JobConfig jobConfig;

    @Override
    public void init(JobConfig jobConfig, Job job) {
        this.jobConfig = jobConfig;
        this.init(job);
    }

    @Override
    public JobConfig getJobConfig() {
        return this.jobConfig;
    }

    @Override
    public void destroy() {
        // empty code
    }

    @Override
    public void failNotice(Job job, String errMsg) {
        Integer execCount = job.getExecCount() + 1;
        log.error("job id：{} 第{}次执行,请注意!", job.getId(), execCount);
    }

    /**
     * 用户自定义初始化方法，可以将后续所有作业任务共用的参数初始化在jobCache中
     */
    public void init(Job job) {
        //用户自定义初始化，可覆盖此方法
    }

    /**
     * 获取该JobProcess对应的JobCode
     * 
     * @return
     */
    public String getJobCode() {
        return this.jobConfig.getJobCode();
    }

    /**
     * 获取JobConfig配置的初始化参数值
     * 
     * @param paramName
     * @return
     */
    public String getInitParam(String paramName) {
        return this.jobConfig.getInitParameter(paramName);
    }

}
