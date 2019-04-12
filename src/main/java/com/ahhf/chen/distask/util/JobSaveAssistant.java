package com.ahhf.chen.distask.util;

import com.ahhf.chen.distask.domain.Job;
import com.ahhf.chen.distask.domain.JobTask;
import com.ahhf.chen.distask.enums.SaveType;

public class JobSaveAssistant {

    public static SaveType getChangedType(Job newJob, Job oldJob) {
        if (oldJob == null) {
            return SaveType.ADD;
        }
        if ("Y".equals(newJob.getIsDeleted())) {
            return SaveType.DELETE;
        }
        if ((newJob.getModuleCode() != null && !newJob.getModuleCode().equals(oldJob.getModuleCode()))
                || (newJob.getJobCode() != null && !newJob.getJobCode().equals(oldJob.getJobCode()))
                || (newJob.getJobName() != null && !newJob.getJobName().equals(oldJob.getJobName()))
                || (newJob.getJobStatus() != null && !newJob.getJobStatus().equals(oldJob.getJobStatus()))
                || (newJob.getExecTime() != null && !newJob.getExecTime().equals(oldJob.getExecTime()))
                || (newJob.getExecCount() != null && !newJob.getExecCount().equals(oldJob.getExecCount()))
                || (newJob.getBusinessNo() != null && !newJob.getBusinessNo().equals(oldJob.getBusinessNo()))
                || (newJob.getJobParams() != null && !newJob.getJobParams().equals(oldJob.getJobParams()))) {
            return SaveType.UPDATE;
        }
        return SaveType.NONE;
    }

    public static SaveType getChangedType(JobTask newTask, JobTask oldTask) {
        if (oldTask == null) {
            return SaveType.ADD;
        }
        if ("Y".equals(newTask.getIsDeleted())) {
            return SaveType.DELETE;
        }
        if ((newTask.getJobId() != null && !newTask.getJobId().equals(oldTask.getJobId()))
                || (newTask.getTaskCode() != null && !newTask.getTaskCode().equals(oldTask.getTaskCode()))
                || (newTask.getTaskName() != null && !newTask.getTaskName().equals(oldTask.getTaskName()))
                || (newTask.getTaskStatus() != null && !newTask.getTaskStatus().equals(oldTask.getTaskStatus()))
                || (newTask.getOrdering() != null && !newTask.getOrdering().equals(oldTask.getOrdering()))
                || (newTask.getTaskParams() != null && !newTask.getTaskParams().equals(oldTask.getTaskParams()))) {
            return SaveType.UPDATE;
        }
        return SaveType.NONE;
    }
}
