package com.ahhf.chen.distask.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.ahhf.chen.distask.dao.dataobject.QueueJobExecLogDO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class JobExecLog extends BaseDomain {

    /**
     * 作业id
     */
    private Long                 jobId;

    /**
     * 执行Job的WokerName
     */
    private String               wokerName;

    /**
     * 作业执行时间
     */
    private Date                 execTime;

    /**
     * 作业执行概要信息（JSON格式存储）
     */
    private String               summaryInfo;

    /**
     * 作业执行错误信息
     */
    private String               errMsg;

    /**
     * 作业任务执行错误堆栈
     */
    private String               errStack;

    /**
     * 作业任务执行Log列表
     */
    private List<JobTaskExecLog> taskLogs;

    public JobExecLog() {
        super();
    }

    public JobExecLog(QueueJobExecLogDO jobLogDO) {
        this.setId(jobLogDO.getId());
        this.setJobId(jobLogDO.getJobId());
        this.setWokerName(jobLogDO.getModifier());
        this.setExecTime(jobLogDO.getExecTime());
        this.setSummaryInfo(jobLogDO.getSummaryInfo());
        this.setErrMsg(jobLogDO.getErrMsg());
        this.setErrStack(jobLogDO.getErrStack());
        this.setOperator(jobLogDO.getManualExecUser());
    }

    public QueueJobExecLogDO convertToDO() {
        QueueJobExecLogDO jobLogDO = new QueueJobExecLogDO();
        jobLogDO.setId(this.getId());
        jobLogDO.setJobId(this.getJobId());
        jobLogDO.setExecTime(this.getExecTime());
        jobLogDO.setManualExecUser(this.getOperator());
        jobLogDO.setSummaryInfo(this.getSummaryInfo());
        jobLogDO.setErrMsg(this.getErrMsg());
        String strErrStack = this.getErrStack();
        if (StringUtils.isNotBlank(strErrStack) && strErrStack.length() > 4000) {
            strErrStack = strErrStack.substring(0, 4000);
        }
        jobLogDO.setErrStack(strErrStack);
        jobLogDO.setCreator(this.getWokerName());
        jobLogDO.setModifier(this.getWokerName());
        return jobLogDO;
    }

    public void addJobTaskExecLog(JobTaskExecLog taskLog) {
        if (taskLog == null) {
            return;
        }
        if (taskLogs == null) {
            taskLogs = new ArrayList<JobTaskExecLog>();
        }
        taskLogs.add(taskLog);
        taskLog.setJobLog(this);
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

    /**
     * 获取Task指定分页的执行Log
     */
    public JobTaskExecLog getTaskLogByStartPos(Long jobTaskId, Integer startPos) {
        if (taskLogs == null || taskLogs.isEmpty() || jobTaskId == null || startPos == null) {
            return null;
        }
        JobTaskExecLog taskLogRes = null;
        for (JobTaskExecLog taskLog : this.taskLogs) {
            if (jobTaskId.equals(taskLog.getJobTaskId()) && startPos.equals(taskLog.getStartPos())) {
                taskLogRes = taskLog;
                break;
            }
        }
        return taskLogRes;
    }

}
