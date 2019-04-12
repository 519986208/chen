package com.ahhf.chen.distask.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.ahhf.chen.distask.dao.dataobject.QueueJobDO;
import com.ahhf.chen.distask.enums.JobStatus;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Job extends BaseDomain {

    /**
     * 模块代码
     */
    private String              moduleCode;

    /**
     * 作业代码
     */
    private String              jobCode;

    /**
     * 作业名
     */
    private String              jobName;

    /**
     * 作业状态，C创建，L锁定，F失败，S成功
     */
    private JobStatus           jobStatus;

    /**
     * 作业最新执行时间
     */
    private Date                execTime;

    /**
     * 作业执行次数
     */
    private Integer             execCount;

    /**
     * 业务号
     */
    private String              businessNo;

    /**
     * 作业参数
     */
    private String              jobParams;

    /**
     * Job的创建者
     */
    private String              creator;

    /**
     * 作业的创建时间
     */
    private Date                createdTime;

    /**
     * 作业关联的任务列表
     */
    private List<JobTask>       jobTasks;

    /**
     * Job级共享缓存
     */
    private Map<String, Object> jobCache = Maps.newHashMap();

    public Job() {
        super();
    }

    public Job(QueueJobDO jobDO) {
        this.setId(jobDO.getId());
        this.setModuleCode(jobDO.getModuleCode());
        this.setJobCode(jobDO.getJobCode());
        this.setJobName(jobDO.getJobName());
        if (StringUtils.isNotBlank(jobDO.getJobStatus())) {
            this.setJobStatus(JobStatus.getByCode(jobDO.getJobStatus()));
        }
        this.setExecTime(jobDO.getExecTime());
        this.setExecCount(jobDO.getExecCount());
        this.setBusinessNo(jobDO.getBusinessNo());
        this.setJobParams(jobDO.getJobParams());
        this.setCreator(jobDO.getCreator());
        this.setCreatedTime(jobDO.getGmtCreated());
        this.setOperator(jobDO.getModifier());
    }

    public QueueJobDO convertToDO() {
        QueueJobDO jobDO = new QueueJobDO();
        jobDO.setId(this.getId());
        jobDO.setModuleCode(this.moduleCode);
        jobDO.setJobCode(this.jobCode);
        jobDO.setJobName(this.jobName);
        if (this.jobStatus != null) {
            jobDO.setJobStatus(this.jobStatus.getCode());
        }
        jobDO.setBusinessNo(this.businessNo);
        jobDO.setExecTime(this.execTime);
        jobDO.setExecCount(this.execCount);
        jobDO.setJobParams(this.jobParams);
        jobDO.setCreator(this.getOperator());
        jobDO.setModifier(this.getOperator());
        if (this.getIsDeleted() != null) {
            jobDO.setIsDeleted(this.getIsDeleted());
        }
        return jobDO;
    }

    /**
     * 将值放到jobCache中
     */
    public void putJobCache(String key, Object value) {
        jobCache.put(key, value);
    }

    /**
     * 获取jobCache中指定key的值
     */
    public <T> T getJobCache(String key) {
        Object obj = jobCache.get(key);
        if (obj == null) {
            return null;
        }
        @SuppressWarnings("unchecked")
        T defValue = (T) obj;
        return defValue;
    }

    public void addJobTask(JobTask jobTask) {
        if (jobTask == null) {
            return;
        }
        if (jobTasks == null) {
            jobTasks = new ArrayList<JobTask>();
        }
        jobTasks.add(jobTask);
        jobTask.setJobId(getId());
        jobTask.setJob(this);
    }

    /**
     * 根据taskCode获取作业任务
     */
    public JobTask getTaskByCode(String taskCode) {
        JobTask taskRes = null;
        if (taskCode != null && this.jobTasks != null) {
            for (JobTask tempTask : this.jobTasks) {
                if (taskCode.equals(tempTask.getTaskCode())) {
                    taskRes = tempTask;
                    break;
                }
            }
        }
        return taskRes;
    }

    /**
     * 根据taskId获取作业任务
     */
    public JobTask getTaskById(Long taskId) {
        JobTask taskRes = null;
        if (taskId != null && this.jobTasks != null) {
            for (JobTask tempTask : this.jobTasks) {
                if (taskId.equals(tempTask.getId())) {
                    taskRes = tempTask;
                    break;
                }
            }
        }
        return taskRes;
    }

    public void setJobTasks(List<JobTask> jobTasks) {
        if (jobTasks == null) {
            return;
        }
        this.jobTasks = jobTasks;
        for (JobTask task : jobTasks) {
            if (task != null && task.getJob() == null) {
                task.setJob(this);
                task.setJobId(this.getId());
            }
        }
    }

    @Override
    public String toString() {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("jobCode", jobCode);
        jsonObj.put("jobStatus", jobStatus);
        jsonObj.put("businessNo", businessNo);
        jsonObj.put("id", getId());
        return jsonObj.toJSONString();
    }

    public static String jobListToString(List<Job> jobList) {
        if (jobList == null) {
            return "NULL";
        }
        if (jobList.size() == 0) {
            return "empty job list";
        }
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("[");
        for (Job job : jobList) {
            strBuf.append(job.toString());
            strBuf.append(",");
        }
        return strBuf.toString().substring(0, strBuf.length() - 1) + "]";
    }

}
