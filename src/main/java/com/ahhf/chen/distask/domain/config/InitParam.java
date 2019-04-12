package com.ahhf.chen.distask.domain.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;
import lombok.NoArgsConstructor;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
@NoArgsConstructor
public class InitParam {

    @XmlElement
    private String paramName;

    @XmlElement
    private String paramValue;

}
