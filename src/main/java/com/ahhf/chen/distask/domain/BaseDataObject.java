package com.ahhf.chen.distask.domain;

import java.util.Date;

import lombok.Data;

@Data
public class BaseDataObject {

    private Long   id;

    private String isDeleted;

    private String modifier;

    private String creator;

    private Date   gmtCreated;

    private Date   gmtModified;

}
