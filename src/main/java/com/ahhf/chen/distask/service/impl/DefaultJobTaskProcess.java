package com.ahhf.chen.distask.service.impl;

import org.springframework.stereotype.Service;

import com.ahhf.chen.distask.domain.JobResult;
import com.ahhf.chen.distask.domain.JobTask;

@Service("DefaultJobTaskProcess")
public class DefaultJobTaskProcess<T> extends AbstractJobTaskProcess<T> {

    @Override
    public JobResult execute(JobTask task, T arg) {
        return new JobResult(true);
    }

}
