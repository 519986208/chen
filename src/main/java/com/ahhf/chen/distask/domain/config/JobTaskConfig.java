package com.ahhf.chen.distask.domain.config;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;
import lombok.NoArgsConstructor;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
@NoArgsConstructor
public class JobTaskConfig {

    @XmlAttribute
    private String          taskCode;   //任务代码

    @XmlAttribute
    private String          taskName;   //任务名称

    @XmlElement(name = "taskProcessBean")
    private String          taskClass;  //任务处理类

    @XmlElement
    private String          ordering;   //执行序号；

    @XmlElement(name = "taskParam")
    private List<InitParam> taskParams; //任务的初始化参数

    public String getInitParameter(String paramName) {
        String paramValue = null;
        if (taskParams == null || paramName == null) {
            return null;
        }
        for (InitParam taskParam : taskParams) {
            if (StringUtils.equals(paramName, taskParam.getParamName())) {
                paramValue = taskParam.getParamValue();
                break;
            }
        }
        return paramValue;
    }

}
