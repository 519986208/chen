package com.ahhf.chen.distask.executor;

import java.io.InputStream;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.ahhf.chen.distask.domain.config.JobConfig;
import com.ahhf.chen.distask.domain.config.JobTaskConfig;
import com.ahhf.chen.distask.domain.config.ModuleJobConfig;
import com.ahhf.chen.distask.enums.ErrorCode;
import com.ahhf.chen.distask.util.DataValidator;
import com.ahhf.chen.distask.util.JaxbUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Component
@Data
@XmlRootElement(name = "jobConfigs")
@XmlAccessorType(XmlAccessType.FIELD)
@Slf4j
public class JobConfigParser {

    private static JobConfigParser instance;

    @Value("${cly.distask.configPath}")
    private String                 configPath;

    @XmlElement(name = "moduleJobConfigs")
    private List<ModuleJobConfig>  moduleJobConfigs;

    public JobConfigParser() {
        super();
    }

    @PostConstruct
    public void initConfig() {
        try {
            if (StringUtils.isBlank(configPath)) {
                log.warn("异步Job任务配置文件路径cly.distask.configPath未配置，任务处理器不会被启动...");
                return;
            }
            InputStream configIn = JobConfigParser.class.getClassLoader().getResourceAsStream(configPath);
            instance = JaxbUtil.converyToJavaBean(configIn, JobConfigParser.class);
            log.info("异步Job任务配置解析结果：{}", JSON.toJSONString(instance));
            //校验配置的合法性
            checkModuleConfig();
            //启动Module模块对应的Boss线程
            for (ModuleJobConfig moduleJobConfig : instance.getModuleJobConfigs()) {
                moduleJobConfig.initModuleJobConfigAndStartBoss();
            }
        } catch (Exception e) {
            log.error("异步Job任务配置文件解析失败，configPath={}，异常：", configPath, e);
        }
    }

    private void checkModuleConfig() {
        if (CollectionUtils.isEmpty(instance.getModuleJobConfigs())) {
            throw new BeanInstantiationException(JobConfigParser.class, "未配置模块异步执行任务");
        }
        String moduleCode = null;
        List<String> moduleCodeList = Lists.newArrayList();
        for (ModuleJobConfig moduleConfig : instance.getModuleJobConfigs()) {
            moduleCode = moduleConfig.getModuleCode();
            if (StringUtils.isBlank(moduleCode)) {
                throw new BeanInstantiationException(JobConfigParser.class, "moduleCode不能为空");
            }
            if (moduleCodeList.contains(moduleCode)) {
                throw new BeanInstantiationException(JobConfigParser.class,
                        ErrorCode.CHECK_MODULE_CONFIG.getErrorMsg(moduleCode, "moduleCode不能重复"));
            }
            if (StringUtils.isNotBlank(moduleConfig.getBossWokerCount())) {
                if (!DataValidator.isPosInteger(moduleConfig.getBossWokerCount())) {
                    throw new BeanInstantiationException(JobConfigParser.class,
                            ErrorCode.CHECK_MODULE_CONFIG.getErrorMsg(moduleCode, "bossWokerCount必须为正整数"));
                }
            }
            if (StringUtils.isNotBlank(moduleConfig.getWokerThreadSize())) {
                if (!DataValidator.isPosInteger(moduleConfig.getWokerThreadSize())) {
                    throw new BeanInstantiationException(JobConfigParser.class,
                            ErrorCode.CHECK_MODULE_CONFIG.getErrorMsg(moduleCode, "wokerThreadSize必须为正整数"));
                }
            }
            checkModuleJobConfig(moduleConfig);
            moduleCodeList.add(moduleCode);
        }
    }

    private void checkModuleJobConfig(ModuleJobConfig moduleConfig) {
        String moduleCode = moduleConfig.getModuleCode();
        if (CollectionUtils.isEmpty(moduleConfig.getJobConfigs())) {
            throw new BeanInstantiationException(JobConfigParser.class,
                    ErrorCode.CHECK_MODULE_CONFIG.getErrorMsg(moduleCode, "模块下JobConfig不能为空"));
        }
        String jobCode = null;
        List<String> jobCodeList = Lists.newArrayList();
        for (JobConfig jobConfig : moduleConfig.getJobConfigs()) {
            jobCode = jobConfig.getJobCode();
            if (StringUtils.isBlank(jobCode)) {
                throw new BeanInstantiationException(JobConfigParser.class,
                        ErrorCode.CHECK_MODULE_CONFIG.getErrorMsg(moduleCode, "jobCode不能为空"));
            }
            if (jobCodeList.contains(jobCode)) {
                throw new BeanInstantiationException(JobConfigParser.class,
                        ErrorCode.CHECK_JOB_CONFIG.getErrorMsg(moduleCode, jobCode, "jobCode不能重复"));
            }
            if (StringUtils.isNotBlank(jobConfig.getLockTime())) {
                if (!DataValidator.isPosInteger(jobConfig.getLockTime())) {
                    throw new BeanInstantiationException(JobConfigParser.class,
                            ErrorCode.CHECK_JOB_CONFIG.getErrorMsg(moduleCode, jobCode, "lockTime必须为正整数"));
                }
            }
            if (StringUtils.isBlank(jobConfig.getJobClass())
                    && CollectionUtils.isEmpty(jobConfig.getJobTaskConfigs())) {
                throw new BeanInstantiationException(JobConfigParser.class, ErrorCode.CHECK_JOB_CONFIG
                        .getErrorMsg(moduleCode, jobCode, "jobProcessBean和jobTaskConfigs不能同时为空"));
            }
            checkModuleJobTaskConfig(moduleCode, jobConfig);
            jobCodeList.add(jobCode);
        }
    }

    private void checkModuleJobTaskConfig(String moduleCode, JobConfig jobConfig) {
        String jobCode = jobConfig.getJobCode();
        if (!CollectionUtils.isEmpty(jobConfig.getJobTaskConfigs())) {
            String taskCode = null;
            List<String> taskCodeList = Lists.newArrayList();
            for (JobTaskConfig taskConfig : jobConfig.getJobTaskConfigs()) {
                taskCode = taskConfig.getTaskCode();
                if (StringUtils.isBlank(taskCode)) {
                    throw new BeanInstantiationException(JobConfigParser.class,
                            ErrorCode.CHECK_JOB_CONFIG.getErrorMsg(moduleCode, jobCode, "taskCode不能为空"));
                }
                if (taskCodeList.contains(taskCode)) {
                    throw new BeanInstantiationException(JobConfigParser.class,
                            ErrorCode.CHECK_Task_CONFIG.getErrorMsg(moduleCode, jobCode, taskCode, "taskCode不能重复"));
                }
                if (StringUtils.isBlank(taskConfig.getTaskClass())) {
                    throw new BeanInstantiationException(JobConfigParser.class, ErrorCode.CHECK_Task_CONFIG
                            .getErrorMsg(moduleCode, jobCode, taskCode, "taskProcessBean不能为空"));
                }
                if (StringUtils.isNotBlank(taskConfig.getOrdering())) {
                    if (!DataValidator.isPosInteger(taskConfig.getOrdering())) {
                        throw new BeanInstantiationException(JobConfigParser.class, ErrorCode.CHECK_Task_CONFIG
                                .getErrorMsg(moduleCode, jobCode, taskCode, "ordering必须为正整数"));
                    }
                }
                taskCodeList.add(taskCode);
            }
        }

    }

    public ModuleJobConfig getModuleJobConfig(String moduleCode) {
        if (CollectionUtils.isEmpty(instance.getModuleJobConfigs()) || StringUtils.isBlank(moduleCode)) {
            return null;
        }
        ModuleJobConfig moduleJobConfig = null;
        for (ModuleJobConfig tempConfig : instance.getModuleJobConfigs()) {
            if (StringUtils.equals(moduleCode, tempConfig.getModuleCode())) {
                moduleJobConfig = tempConfig;
                break;
            }
        }
        return moduleJobConfig;
    }

    public JobConfig getJobConfig(String moduleCode, String jobCode) {
        ModuleJobConfig moduleJobConfig = this.getModuleJobConfig(moduleCode);
        if (moduleJobConfig == null) {
            return null;
        }
        return moduleJobConfig.getJobConfig(jobCode);
    }

    public List<ModuleJobConfig> getJobModuleJobConfigs() {
        if (instance == null) {
            return null;
        }
        return instance.getModuleJobConfigs();
    }
}
