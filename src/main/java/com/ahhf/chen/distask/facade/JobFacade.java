package com.ahhf.chen.distask.facade;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.ahhf.chen.distask.domain.BaseResult;
import com.ahhf.chen.distask.domain.Job;
import com.ahhf.chen.distask.domain.JobTask;
import com.ahhf.chen.distask.domain.config.JobConfig;
import com.ahhf.chen.distask.domain.config.JobTaskConfig;
import com.ahhf.chen.distask.enums.ErrorCode;
import com.ahhf.chen.distask.enums.JobStatus;
import com.ahhf.chen.distask.enums.JobTaskStatus;
import com.ahhf.chen.distask.exception.BusinessException;
import com.ahhf.chen.distask.exception.ExceptionUtils;
import com.ahhf.chen.distask.executor.JobBossStartListener;
import com.ahhf.chen.distask.executor.JobConfigParser;
import com.ahhf.chen.distask.service.JobService;
import com.alibaba.fastjson.JSON;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class JobFacade {

    @Resource
    private JobConfigParser      jobConfigParser;

    @Resource
    private JobService           jobService;

    @Resource
    private JobBossStartListener jobBossStartListener;

    public JobFacade() {
        super();
    }

    /**
     * 新提交Job任务
     */
    public BaseResult<Long> submitJob(JobDTO jobDTO) {
        BaseResult<Long> result = new BaseResult<>();
        log.info("新提交Job任务，jobDTO={}", JSON.toJSONString(jobDTO));
        try {
            Job job = checkAndInitJob(jobDTO);
            Long jobId = jobService.saveAndModifyJob(job);
            result.setSuccess(jobId);
        } catch (BusinessException e) {
            log.warn("提交Job业务异常：", e);
            result.setCode(e.getErrorCode());
            result.setMessage(e.getErrorMsg());
        } catch (Exception e) {
            log.warn("提交Job未知异常：", e);
            result.setCode(ErrorCode.CMN_RUNTIME_ERROR.getCode());
            result.setMessage(ErrorCode.CMN_RUNTIME_ERROR.getErrorMsg(e.getMessage()));
        }
        return result;
    }

    private Job checkAndInitJob(JobDTO jobDTO) {
        if (jobDTO == null) {
            throw ExceptionUtils.throwException(ErrorCode.CMN_PARAMETER_ERROR, "jobDTO不能为null");
        }
        String moduleCode = jobDTO.getModuleCode();
        if (StringUtils.isBlank(jobDTO.getModuleCode())) {
            throw ExceptionUtils.throwException(ErrorCode.CMN_PARAMETER_ERROR, "jobDTO.moduleCode不能为空");
        }
        String jobCode = jobDTO.getJobCode();
        if (StringUtils.isBlank(jobCode)) {
            throw ExceptionUtils.throwException(ErrorCode.CMN_PARAMETER_ERROR, "jobDTO.jobCode不能为空");
        }
        if (StringUtils.isBlank(jobDTO.getBusinessNo())) {
            throw ExceptionUtils.throwException(ErrorCode.CMN_PARAMETER_ERROR, "jobDTO.businessNo不能为空");
        }
        JobConfig jobConfig = jobConfigParser.getJobConfig(jobDTO.getModuleCode(), jobDTO.getJobCode());
        if (jobConfig == null) {
            throw ExceptionUtils.throwException(ErrorCode.SUBMIT_JOB_ERROR, moduleCode, jobCode, "不存在该Job配置");
        }
        if (!CollectionUtils.isEmpty(jobConfig.getJobTaskConfigs()) && CollectionUtils.isEmpty(jobDTO.getTasks())) {
            throw ExceptionUtils.throwException(ErrorCode.SUBMIT_JOB_ERROR, moduleCode, jobCode,
                    "Job配置下存在Task配置，必须提交taskDTO参数");
        }
        Job job = new Job();
        job.setId(jobDTO.getId());
        job.setModuleCode(jobDTO.getModuleCode());
        job.setJobCode(jobDTO.getJobCode());
        job.setJobName(jobConfig.getJobName());
        job.setJobStatus(JobStatus.CREATE);
        job.setExecTime(new Date());
        job.setExecCount(Integer.valueOf(0));
        job.setBusinessNo(jobDTO.getBusinessNo());
        job.setJobParams(jobDTO.getJobParams());
        job.setOperator(jobDTO.getOperator());
        if (!CollectionUtils.isEmpty(jobDTO.getTasks())) {
            JobTaskConfig taskConfig = null;
            JobTask task = null;
            for (JobTaskDTO taskDTO : jobDTO.getTasks()) {
                String taskCode = taskDTO.getTaskCode();
                if (StringUtils.isBlank(taskCode)) {
                    throw ExceptionUtils.throwException(ErrorCode.CMN_PARAMETER_ERROR, "taskDTO.taskCode不能为空");
                }
                taskConfig = jobConfig.getJobTaskConfig(taskCode);
                if (taskConfig == null) {
                    throw ExceptionUtils.throwException(ErrorCode.SUBMIT_Task_ERROR, moduleCode, jobCode, taskCode,
                            "Job配置下不存在该Task配置");
                }
                task = new JobTask();
                task.setId(taskDTO.getId());
                task.setTaskCode(taskDTO.getTaskCode());
                task.setTaskName(taskConfig.getTaskName());
                task.setTaskStatus(JobTaskStatus.CREATE);
                task.setTaskParams(taskDTO.getTaskParams());
                if (StringUtils.isNotBlank(taskConfig.getOrdering())) {
                    task.setOrdering(Integer.valueOf(taskConfig.getOrdering()));
                }
                if (taskDTO.getOrdering() != null) {
                    task.setOrdering(taskDTO.getOrdering());
                }
                if (StringUtils.isNotEmpty(taskDTO.getTaskName())) {
                    task.setTaskName(taskDTO.getTaskName());
                }
                task.setOperator(jobDTO.getOperator());
                job.addJobTask(task);
            }
        }
        return job;
    }
}
