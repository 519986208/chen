package com.ahhf.chen.distask.domain;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.ahhf.chen.distask.dao.dataobject.QueueJobTaskDO;
import com.ahhf.chen.distask.enums.JobTaskStatus;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.google.common.collect.Maps;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class JobTask extends BaseDomain {

    /**
     * 作业id
     */
    private Long                jobId;

    /**
     * 作业任务代码
     */
    private String              taskCode;

    /**
     * 作业任务名
     */
    private String              taskName;

    /**
     * 任务状态，C创建，R运行，F失败，S成功
     */
    private JobTaskStatus       taskStatus;

    /**
     * 任务参数
     */
    private String              taskParams;

    /**
     * 任务顺序
     */
    private Integer             ordering;

    /**
     * 所属Job
     */
    @JSONField(serialize = false)
    private Job                 job;

    /**
     * Task级共享缓存
     */
    private Map<String, Object> taskCache = Maps.newHashMap();

    public JobTask() {
        super();
    }

    public JobTask(QueueJobTaskDO taskDO) {
        this.setId(taskDO.getId());
        this.setJobId(taskDO.getJobId());
        this.setTaskCode(taskDO.getTaskCode());
        this.setTaskName(taskDO.getTaskName());
        if (StringUtils.isNotBlank(taskDO.getTaskStatus())) {
            this.setTaskStatus(JobTaskStatus.getByCode(taskDO.getTaskStatus()));
        }
        this.setTaskParams(taskDO.getTaskParams());
        this.setOrdering(taskDO.getOrdering());
        this.setOperator(taskDO.getModifier());
    }

    public QueueJobTaskDO convertToDO() {
        QueueJobTaskDO taskDO = new QueueJobTaskDO();
        taskDO.setId(this.getId());
        taskDO.setJobId(this.jobId);
        taskDO.setTaskCode(this.taskCode);
        taskDO.setTaskName(this.taskName);
        if (this.taskStatus != null) {
            taskDO.setTaskStatus(this.taskStatus.getCode());
        }
        taskDO.setTaskParams(this.taskParams);
        taskDO.setOrdering(this.ordering);
        taskDO.setCreator(this.getOperator());
        taskDO.setModifier(this.getOperator());
        if (this.getIsDeleted() != null) {
            taskDO.setIsDeleted(this.getIsDeleted());
        }
        return taskDO;
    }

    /**
     * 将值放到taskCache中
     */
    public void putTaskCache(String key, Object value) {
        taskCache.put(key, value);
    }

    /**
     * 获取taskCache中指定key的值，如果taskCache中无对应值，则从jobCache中获取
     */
    public <T> T getTaskCache(String key) {
        Object obj = taskCache.get(key);
        if (obj == null) {
            if (this.job == null) {
                return null;
            } else {
                T defValue = this.job.getJobCache(key);
                return defValue;
            }
        } else {
            @SuppressWarnings("unchecked")
            T defValue = (T) obj;
            return defValue;
        }
    }

    @Override
    public String toString() {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("id", getId());
        jsonObj.put("taskCode", taskCode);
        jsonObj.put("taskStatus", taskStatus);
        jsonObj.put("jobId", jobId);
        return jsonObj.toJSONString();
    }

    public static String taskListToString(List<JobTask> taskList) {
        if (taskList == null) {
            return "NULL";
        }
        if (taskList.size() == 0) {
            return "empty task list";
        }
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("[");
        for (JobTask task : taskList) {
            strBuf.append(task.toString());
            strBuf.append(",");
        }
        return strBuf.toString().substring(0, strBuf.length() - 1) + "]";
    }

}
