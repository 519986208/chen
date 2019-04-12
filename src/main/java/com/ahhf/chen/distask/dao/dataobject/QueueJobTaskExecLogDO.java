package com.ahhf.chen.distask.dao.dataobject;

import java.util.Date;

import com.ahhf.chen.distask.domain.BaseDataObject;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class QueueJobTaskExecLogDO extends BaseDataObject {

    /**
     * 作业执行日志id
     */
    private Long    jobExecLogId;

    /**
     * 作业id
     */
    private Long    jobId;

    /**
     * 作业任务id
     */
    private Long    jobTaskId;

    /**
     * 作业任务执行的起始位置
     */
    private Integer startPos;

    /**
     * 作业任务执行时间
     */
    private Date    execTime;

    /**
     * 作业任务执行概要信息（JSON格式存储）
     */
    private String  summaryInfo;

    /**
     * 作业任务执行错误信息
     */
    private String  errMsg;

    /**
     * 作业任务执行错误堆栈
     */
    private String  errStack;

}
