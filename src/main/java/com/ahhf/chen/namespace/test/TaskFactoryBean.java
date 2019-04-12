package com.ahhf.chen.namespace.test;

import java.util.UUID;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

import com.ahhf.chen.namespace.bean.TaskBean;

@Component
public class TaskFactoryBean implements FactoryBean<TaskBean> {

    @Override
    public TaskBean getObject() throws Exception {
        TaskBean task = new TaskBean();
        task.setId(13456L);
        task.setName(UUID.randomUUID().toString());
        task.setPriority("6");
        return task;
    }

    @Override
    public Class<TaskBean> getObjectType() {
        return TaskBean.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
