package com.ahhf.chen.distask.facade;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class JobDTO {

    private Long             id;

    /**
     * 模块代码
     */
    private String           moduleCode;

    /**
     * 作业代码
     */
    private String           jobCode;

    /**
     * 业务号
     */
    private String           businessNo;

    /**
     * 作业参数
     */
    private String           jobParams;

    /**
     * 操作人员
     */
    private String           operator;

    /**
     * 作业关联的任务列表
     */
    private List<JobTaskDTO> tasks;

    public void addJobTask(JobTaskDTO jobTask) {
        if (jobTask == null) {
            return;
        }
        if (tasks == null) {
            tasks = new ArrayList<JobTaskDTO>();
        }
        tasks.add(jobTask);
    }

}
