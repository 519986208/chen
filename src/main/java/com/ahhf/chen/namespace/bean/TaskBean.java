package com.ahhf.chen.namespace.bean;

import lombok.Data;

@Data
public class TaskBean {

    private Long     id;

    private String   name;

    private String   priority;

    private TaskBean relativeTask;

}
