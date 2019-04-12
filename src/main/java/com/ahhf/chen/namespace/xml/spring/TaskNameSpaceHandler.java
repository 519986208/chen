package com.ahhf.chen.namespace.xml.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

import com.ahhf.chen.namespace.xml.TaskXmlParser;

public class TaskNameSpaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        //对应xml中的标签     <distask:task> <命名空间：标签>
        registerBeanDefinitionParser("task", new TaskXmlParser());
    }

}
