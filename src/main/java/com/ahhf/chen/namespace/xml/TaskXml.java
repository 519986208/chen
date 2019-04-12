package com.ahhf.chen.namespace.xml;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.ahhf.chen.namespace.bean.TaskBean;

import lombok.Data;

@Data
public class TaskXml implements ApplicationContextAware, FactoryBean<TaskBean> {

    private Long               id;

    private String             name;

    private String             priority;

    private String             relativeTask;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public TaskBean getObject() throws Exception {
        TaskBean tb = new TaskBean();
        tb.setId(id);
        tb.setName(name);
        tb.setPriority(priority);
        if (StringUtils.isNotEmpty(relativeTask)) {
            TaskBean bean = applicationContext.getBean(relativeTask, getObjectType());
            tb.setRelativeTask(bean);
        }
        System.out.println("get bean ========== " + name);
        return tb;
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
