package com.ahhf.chen.distask.facade;

import lombok.Data;

@Data
public class JobTaskDTO {

    private Long    id;

    /**
     * 作业任务代码
     */
    private String  taskCode;

    /**
     * 任务参数
     */
    private String  taskParams;

    /**
     * 任务优先级
     */
    private Integer ordering;

    /**
     * 任务名称
     */
    private String  taskName;

}
