package com.ahhf.chen.distask.domain.config;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;
import lombok.NoArgsConstructor;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
@NoArgsConstructor
public class JobConfig {

    @XmlAttribute
    private String              jobCode;        //作业代码

    @XmlAttribute
    private String              jobName;        //作业名称

    @XmlElement(name = "jobProcessBean")
    private String              jobClass;       //作业处理类

    @XmlElement
    private String              lockTime;       //锁定时间（单位：分钟），如超过锁定时间则强制解锁重新执行；

    @XmlElementWrapper(name = "jobTaskConfigs")
    @XmlElement(name = "jobTaskConfig")
    private List<JobTaskConfig> jobTaskConfigs; //作业下任务配置列表

    @XmlElement(name = "jobParam")
    private List<InitParam>     jobParams;      //作业的初始化参数

    public JobTaskConfig getJobTaskConfig(String taskCode) {
        JobTaskConfig taskConfigRes = null;
        if (jobTaskConfigs == null || taskCode == null) {
            return null;
        }
        for (JobTaskConfig temp : this.jobTaskConfigs) {
            if (StringUtils.equals(taskCode, temp.getTaskCode())) {
                taskConfigRes = temp;
                break;
            }
        }
        return taskConfigRes;
    }

    public String getInitParameter(String paramName) {
        String paramValue = null;
        if (jobParams == null || paramName == null) {
            return null;
        }
        for (InitParam jobParam : jobParams) {
            if (StringUtils.equals(paramName, jobParam.getParamName())) {
                paramValue = jobParam.getParamValue();
                break;
            }
        }
        return paramValue;
    }

}
