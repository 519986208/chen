package com.ahhf.chen.distask.domain.config;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.ahhf.chen.distask.executor.JobBoss;

import lombok.Data;
import lombok.NoArgsConstructor;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
@NoArgsConstructor
public class ModuleJobConfig {

    @XmlAttribute
    private String                 moduleCode;                                               //模块代码

    @XmlAttribute
    private String                 moduleName;                                               //模块名称

    @XmlAttribute
    private String                 bossWokerCount;                                           //Boss所启动的Worker数量

    @XmlAttribute
    private String                 wokerThreadSize;                                          //Worker执行线程数量

    @XmlElement(name = "jobConfig")
    private List<JobConfig>        jobConfigs;

    private Map<String, JobConfig> jobConfigMap = new ConcurrentHashMap<String, JobConfig>();

    private JobBoss                moduleJobBoss;

    public void initModuleJobConfigAndStartBoss() {
        if (jobConfigs == null || jobConfigs.isEmpty()) {
            return;
        }
        for (JobConfig jobConfig : this.getJobConfigs()) {
            this.getJobConfigMap().put(jobConfig.getJobCode(), jobConfig);
        }
    }

    public JobConfig getJobConfig(String jobCode) {
        return this.getJobConfigMap().get(jobCode);
    }

}
