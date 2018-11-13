package com.ahhf.chen.test.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class StudentAdvisor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation method) throws Throwable {
        System.out.println("advisor " + method.getMethod().getName());
        return method.proceed();
    }

}
