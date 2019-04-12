package com.ahhf.chen.distask.service.impl;

import org.springframework.stereotype.Service;

import com.ahhf.chen.distask.domain.Job;
import com.ahhf.chen.distask.domain.JobResult;

@Service("DefaultJobProcess")
public class DefaultJobProcess extends AbstractJobProcess {

    @Override
    public JobResult execute(Job job) {
        return new JobResult(true);
    }

}
