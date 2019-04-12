package com.ahhf.chen.distask.util;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ServiceManager implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public static <T> T service(Class<T> clz) {
        return context().getBean(clz);
    }

    public static <T> T bean(Class<T> beanClz, String name) {
        return context().getBean(name, beanClz);
    }

    public static <T> T bean(Class<T> beanClz) {
        return context().getBean(beanClz);
    }

    public static <T> Map<String, T> beans(Class<T> beanClz) {
        return context().getBeansOfType(beanClz);
    }

    public static ApplicationContext context() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ServiceManager.applicationContext = applicationContext;
    }

}
