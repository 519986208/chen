package com.ahhf.chen.distask.dao.dataobject;

import java.util.Date;

import com.ahhf.chen.distask.domain.BaseDataObject;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class QueueJobDO extends BaseDataObject {

    /**
     * 模块代码
     */
    private String  moduleCode;

    /**
     * 作业代码
     */
    private String  jobCode;

    /**
     * 作业名
     */
    private String  jobName;

    /**
     * 作业状态，C创建，T提交，L锁定，F失败，S成功
     */
    private String  jobStatus;

    /**
     * 业务号
     */
    private String  businessNo;

    /**
     * 作业最新执行时间
     */
    private Date    execTime;

    /**
     * 作业执行次数
     */
    private Integer execCount;

    /**
     * 作业参数
     */
    private String  jobParams;

}
