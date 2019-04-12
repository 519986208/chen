package com.ahhf.chen.distask.domain;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.ahhf.chen.distask.dao.dataobject.QueueJobTaskExecLogDO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class JobTaskExecLog extends BaseDomain {

    /**
     * 作业任务id
     */
    private Long       jobTaskId;

    /**
     * 作业任务执行的起始位置
     */
    private Integer    startPos;

    /**
     * 作业任务执行时间
     */
    private Date       execTime;

    /**
     * 作业任务执行概要信息（JSON格式存储）
     */
    private String     summaryInfo;

    /**
     * 作业任务执行错误信息
     */
    private String     errMsg;

    /**
     * 作业任务执行错误堆栈
     */
    private String     errStack;

    /**
     * 关联的作业Log
     */
    private JobExecLog jobLog;

    public JobTaskExecLog() {
        super();
    }

    public JobTaskExecLog(QueueJobTaskExecLogDO taskLogDO) {
        this.setId(taskLogDO.getId());
        this.setJobTaskId(taskLogDO.getJobTaskId());
        this.setStartPos(taskLogDO.getStartPos());
        this.setExecTime(taskLogDO.getExecTime());
        this.setSummaryInfo(taskLogDO.getSummaryInfo());
        this.setErrMsg(taskLogDO.getErrMsg());
        this.setErrStack(taskLogDO.getErrStack());
        this.setOperator(taskLogDO.getModifier());
        JobExecLog jobLog = new JobExecLog();
        jobLog.setId(taskLogDO.getJobExecLogId());
        jobLog.setJobId(taskLogDO.getJobId());
        this.setJobLog(jobLog);
    }

    public QueueJobTaskExecLogDO convertToDO() {
        QueueJobTaskExecLogDO jobTaskLogDO = new QueueJobTaskExecLogDO();
        jobTaskLogDO.setId(this.getId());
        jobTaskLogDO.setJobExecLogId(this.getJobLog().getId());
        jobTaskLogDO.setJobId(this.getJobLog().getJobId());
        jobTaskLogDO.setJobTaskId(this.getJobTaskId());
        jobTaskLogDO.setStartPos(this.getStartPos());
        jobTaskLogDO.setExecTime(this.getExecTime());
        jobTaskLogDO.setSummaryInfo(this.getSummaryInfo());
        jobTaskLogDO.setErrMsg(this.getErrMsg());
        String strErrStack = this.getErrStack();
        if (StringUtils.isNotBlank(strErrStack) && strErrStack.length() > 4000) {
            strErrStack = strErrStack.substring(0, 4000);
        }
        jobTaskLogDO.setErrStack(strErrStack);
        jobTaskLogDO.setCreator(this.getOperator());
        jobTaskLogDO.setModifier(this.getOperator());
        return jobTaskLogDO;
    }

    public void setSummaryData(String key, String value) {
        JSONObject jsonObj = null;
        if (StringUtils.isBlank(key)) {
            return;
        }
        if (StringUtils.isBlank(summaryInfo)) {
            jsonObj = new JSONObject();
        } else {
            jsonObj = JSON.parseObject(summaryInfo);
        }
        jsonObj.put(key, value);
        this.summaryInfo = jsonObj.toJSONString();
    }

    public String getSummaryData(String key) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(summaryInfo)) {
            return null;
        }
        JSONObject jsonObj = JSON.parseObject(summaryInfo);
        return jsonObj.getString(key);
    }

}
