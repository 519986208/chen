package com.ahhf.chen.distask.service;

import java.util.List;

import com.ahhf.chen.distask.domain.JobResult;
import com.ahhf.chen.distask.domain.JobTask;
import com.ahhf.chen.distask.domain.config.JobTaskConfig;

/**
 * 类JobTaskProcess.java的实现描述：Task处理器接口，需要自定义实现
 */
public interface JobTaskProcess<T> {
    /**
     * Task处理器初始化方法，由异步作业引擎在执行过程中调用，将taskConfig配置信息初始化给Task处理器
     * 
     * @param taskConfig Task处理器共用，可设为成员变量
     * @param task 每个任务的执行参数，不可设为成员变量
     */
    void init(JobTaskConfig taskConfig, JobTask task);

    /**
     * 获取JobTaskConfig信息
     */
    JobTaskConfig getTaskConfig();

    /**
     * Task处理器需要处理的数据量<br>
     * 如无需通过load方法加载数据可直接返回0
     */
    int count(JobTask task);

    /**
     * 作业引擎任务处理器每次加载需要处理的对象数据列表
     */
    List<T> load(JobTask task, int start, int limit);

    /**
     * 作业引擎调用此方法处理任务的实际执行逻辑
     * 
     * @param task
     * @param arg，如count()方法返回0，则该arg参数为null
     */
    JobResult execute(JobTask task, T arg);

    /**
     * 如涉及外部资源，在Task处理器销毁时，可以在此处释放外部资源
     */
    void destroy();
}
