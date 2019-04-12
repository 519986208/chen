package com.ahhf.chen.distask.service;

import com.ahhf.chen.distask.domain.Job;
import com.ahhf.chen.distask.domain.JobResult;
import com.ahhf.chen.distask.domain.config.JobConfig;

/**
 * 类JobProcess.java的实现描述：Job的处理器接口，可以自定义实现
 */
public interface JobProcess {

    /**
     * Job处理器初始化方法，由异步作业引擎在执行过程中调用，将JobConfig配置信息初始化给Job处理器
     * 
     * @param jobConfig Job处理器共用，可设为成员变量
     * @param job 每个作业的执行参数，不可设为成员变量
     */
    void init(JobConfig jobConfig, Job job);

    /**
     * 获取JobConfig信息
     */
    JobConfig getJobConfig();

    /**
     * 由异步作业引擎调用此方法处理作业的实际业务
     */
    JobResult execute(Job job);

    /**
     * Job执行失败通知，由业务方实现具体通知逻辑，如邮件或其它
     */
    void failNotice(Job job, String errMsg);

    /**
     * 如涉及外部资源，在Job处理器销毁时，可以在此处释放外部资源
     */
    void destroy();
}
