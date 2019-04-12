package com.ahhf.chen.distask.dao;

import java.util.List;

import com.ahhf.chen.distask.dao.dataobject.QueueJobTaskDO;

public interface QueueJobTaskDAO {

    int insert(QueueJobTaskDO record);

    QueueJobTaskDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(QueueJobTaskDO record);

    /**
     * 根据JobId查询所有任务列表
     * 
     * @param jobId
     * @return
     */
    List<QueueJobTaskDO> selectTasksByJobId(Long jobId);

    /**
     * 更新JobTask的任务状态
     */
    int updateTaskStatusByID(Long taskId, String newStatus, String modifier);

}
