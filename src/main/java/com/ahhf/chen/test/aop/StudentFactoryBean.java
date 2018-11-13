package com.ahhf.chen.test.aop;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.NameMatchMethodPointcutAdvisor;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

import com.ahhf.chen.context.ChenContext;
import com.ahhf.chen.test.bean.Student;
import com.ahhf.chen.test.service.StudentService;

@Component("stu")
public class StudentFactoryBean implements FactoryBean<StudentService> {

    private String mappedName = "study";//* 表示所有方法

    @Override
    public StudentService getObject() throws Exception {
        ProxyFactory weaver = new ProxyFactory(ChenContext.getBean("studentService", StudentService.class));
        NameMatchMethodPointcutAdvisor advisor = new NameMatchMethodPointcutAdvisor();
        advisor.setMappedName(mappedName);
        advisor.setAdvice(new StudentAdvisor());
        weaver.addAdvisor(advisor);
        StudentService proxyObject = (StudentService) weaver.getProxy();
        // 返回执行结果
        return proxyObject;
    }

    @Override
    public Class<Student> getObjectType() {
        return Student.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
