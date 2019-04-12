package com.ahhf.chen.distask.dao;

import java.util.List;

import com.ahhf.chen.distask.dao.dataobject.QueueJobDO;

public interface QueueJobDAO {

    int insert(QueueJobDO record);

    QueueJobDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(QueueJobDO record);

    //分页查询3天内待处理作业列表
    List<QueueJobDO> selectPendingJobByPage(String moduleCode, Integer start, Integer limit);

    //根据作业的Id和旧状态更新为新状态
    int updateJobStatusByID(Long jobId, String oldStatus, String modifier, String newStatus, String oldModifier);

    //根据作业的Id更新Job执行次数加1
    int updateJobExecCountByID(Long jobId, String modifier);

    //根据业务号查询Job
    QueueJobDO selectJobByBusinessNo(String moduleCode, String businessNo, String jobCode);

}
