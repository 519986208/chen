package com.ahhf.chen.distask.dao;

import com.ahhf.chen.distask.dao.dataobject.QueueJobExecLogDO;

public interface QueueJobExecLogDAO {

    int insert(QueueJobExecLogDO record);

    QueueJobExecLogDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(QueueJobExecLogDO record);

    /**
     * 根据JobId查询离当前时间最近的一条执行日志
     */
    QueueJobExecLogDO selectLatestLogByJobId(Long jobId);

}
