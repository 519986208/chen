package com.ahhf.chen.test;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.ahhf.chen.namespace.bean.TaskBean;
import com.ahhf.chen.namespace.config.TaskConfigImport;
import com.alibaba.fastjson.JSON;

public class TestNameSpace {

    public static void main(String[] args) {
        AbstractApplicationContext context = new AnnotationConfigApplicationContext(TaskConfigImport.class);

        for (int i = 0; i < 5; i++) {
            Map<String, TaskBean> beansOfType = context.getBeansOfType(TaskBean.class);
            Set<Entry<String, TaskBean>> entrySet = beansOfType.entrySet();
            for (Entry<String, TaskBean> entry : entrySet) {
                System.out.println(JSON.toJSONString(entry.getValue()));
            }
        }
        context.close();
    }

}
