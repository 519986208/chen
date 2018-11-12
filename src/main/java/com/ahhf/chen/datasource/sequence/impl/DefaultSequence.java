package com.ahhf.chen.datasource.sequence.impl;

import java.sql.SQLException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.ahhf.chen.datasource.sequence.Sequence;
import com.ahhf.chen.datasource.sequence.SequenceDao;
import com.ahhf.chen.datasource.sequence.SequenceException;
import com.ahhf.chen.datasource.sequence.SequenceRange;

import lombok.extern.slf4j.Slf4j;

/**
 * 序列默认实现
 */
@Slf4j
public class DefaultSequence implements Sequence {

    private final Lock             lock = new ReentrantLock();

    private SequenceDao            sequenceDao;

    /**
     * 序列名称
     */
    private String                 name;

    private volatile SequenceRange currentRange;

    public void init() throws SequenceException, SQLException {
        if (!(sequenceDao instanceof DefaultSequenceDao)) {
            throw new SequenceException("please use  DefaultSequenceDao!");
        }
        DefaultSequenceDao groupSequenceDao = (DefaultSequenceDao) sequenceDao;
        synchronized (this) {
            groupSequenceDao.adjust(name);
        }
    }

    @Override
    public long nextValue() {
        try {
            if (currentRange == null) {
                lock.lock();
                try {
                    if (currentRange == null) {
                        currentRange = sequenceDao.nextRange(name);
                    }
                } finally {
                    lock.unlock();
                }
            }

            long value = currentRange.getAndIncrement();
            if (value == -1) {
                lock.lock();
                try {
                    for (;;) {
                        if (currentRange.isOver()) {
                            currentRange = sequenceDao.nextRange(name);
                        }

                        value = currentRange.getAndIncrement();
                        if (value == -1) {
                            continue;
                        }

                        break;
                    }
                } finally {
                    lock.unlock();
                }
            }

            if (value < 0) {
                throw new SequenceException("Sequence value overflow, value = " + value);
            }

            return value;
        } catch (SequenceException e) {
            log.error("get Sequence next value error !", e);
            throw new SequenceException("get Sequence next value error !", e);
        }
    }

    public SequenceDao getSequenceDao() {
        return sequenceDao;
    }

    public void setSequenceDao(SequenceDao sequenceDao) {
        this.sequenceDao = sequenceDao;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
