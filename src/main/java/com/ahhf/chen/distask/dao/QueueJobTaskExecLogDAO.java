package com.ahhf.chen.distask.dao;

import com.ahhf.chen.distask.dao.dataobject.QueueJobTaskExecLogDO;

public interface QueueJobTaskExecLogDAO {

    int insert(QueueJobTaskExecLogDO record);

    QueueJobTaskExecLogDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(QueueJobTaskExecLogDO record);

    /**
     * 根据jobLogId、taskId、任务执行的起始位置查询Task单页数据处理日志
     */
    QueueJobTaskExecLogDO selectTaskPageLogByTaskIdAndStartPos(Long jobExecLogId, Long jobTaskId, Integer startPos);

}
