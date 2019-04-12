package com.ahhf.chen.distask.dao.dataobject;

import com.ahhf.chen.distask.domain.BaseDataObject;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class QueueJobTaskDO extends BaseDataObject {
    /**
     * 作业id
     */
    private Long    jobId;

    /**
     * 作业任务代码
     */
    private String  taskCode;

    /**
     * 作业任务名
     */
    private String  taskName;

    /**
     * 任务状态，C创建，R运行，F失败，S成功
     */
    private String  taskStatus;

    /**
     * 任务参数
     */
    private String  taskParams;

    /**
     * 任务顺序
     */
    private Integer ordering;

}
