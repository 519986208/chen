package com.ahhf.chen.distask.domain;

import com.ahhf.chen.distask.domain.config.JobConfig;
import com.ahhf.chen.distask.enums.JobStatus;
import com.ahhf.chen.distask.service.JobProcess;

import lombok.Data;

/**
 * 类JobContext.java的实现描述：Job执行上下文，包含执行的Job对象和配置
 */
@Data
public class JobContext {

    private Job          job;

    /**
     * Job的配置信息
     */
    private JobConfig    jobConfig;

    /**
     * Job处理器
     */
    private JobProcess   jobProcess;

    /**
     * Job的最终状态
     */
    private JobStatus    finalJobStatus = JobStatus.SUCCESS;

    /**
     * Job的最终错误详情（包含Task的error）
     */
    private StringBuffer finalErrBuf    = new StringBuffer();

    /**
     * 有依赖关系的Task是否执行失败
     */
    private boolean      dependTaskFail = false;

    public JobContext() {
        super();
    }

    public JobContext(Job job) {
        super();
        this.job = job;
    }

    /**
     * 追加错误日志
     * 
     * @param errMsg
     */
    public void appendError(String errMsg) {
        this.finalErrBuf.append(errMsg);
        this.finalErrBuf.append("\r\n");
    }

}
