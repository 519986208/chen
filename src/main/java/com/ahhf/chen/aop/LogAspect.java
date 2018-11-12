package com.ahhf.chen.aop;

import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Service;

import com.ahhf.chen.context.WebContext;
import com.alibaba.fastjson.JSON;

import lombok.extern.slf4j.Slf4j;

/**
 * 定义切面，记录日志<br>
 * 拦截http请求与返回值
 */
@Service
@Aspect
@Slf4j
public class LogAspect {

    /**
     * 线程级别，放置环绕通知的请求
     */
    private ThreadLocal<StringBuilder> httpRequest = new ThreadLocal<>();

    private ThreadLocal<StopWatch>     stopWatch   = new ThreadLocal<>();

    //    @Pointcut("execution(public * com.ahhf.chen.service.*Impl.*(..))")
    @Pointcut("execution(public * com.ahhf.chen.test.controller.*Controller.*(..))")
    public void pointCutMethod() {
        log.info("pointCutMethod");
    }

    /**
     * 声明后置通知<br>
     * 返回方法的返回值<br>
     * 记录到日志文件中
     * 
     * @param returnValue
     */
    @AfterReturning(pointcut = "pointCutMethod()", returning = "returnValue")
    public void after(Object returnValue) {
        StringBuilder builder = httpRequest.get();
        builder.append("方法返回值：");
        builder.append(JSON.toJSONString(returnValue));
        StopWatch sw = stopWatch.get();
        if (sw != null) {
            sw.stop();
            long milliseconds = sw.getTime();
            builder.append("\n");
            builder.append("请求总时间:");
            builder.append(milliseconds + "毫秒");
        }
        builder.append("\n");
        log.info(builder.toString());
        stopWatch.remove();
        httpRequest.remove();
    }

    /**
     * 声明环绕通知<br>
     * 拦截请求路径<br>
     * 拦截http请求方法<br>
     * 拦截类名<br>
     * 拦截方法名<br>
     * 拦截请求参数<br>
     * 拦截RequestBody
     * 
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("pointCutMethod()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch sw = new StopWatch();
        sw.start();
        stopWatch.set(sw);

        String requestPath = WebContext.getRequestPath();
        String method = WebContext.getRequest().getMethod();
        String targetName = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] arguments = joinPoint.getArgs();
        StringBuilder builder = new StringBuilder();
        builder.append("\n");
        builder.append("请求路径：" + requestPath).append("\n");
        builder.append("http请求方式：" + method).append("\n");
        builder.append("类名：" + targetName).append("\n");
        builder.append("方法名：" + methodName).append("\n");
        builder.append("参数：");

        int length = arguments.length;
        for (int i = 0; i < length; i++) {
            Object obj = arguments[i];
            if (obj == null) {
                continue;
            }
            //需要排除一些对象
            String value = JSON.toJSONString(obj);
            if (i == length - 1) {
                builder.append(value);
            } else {
                builder.append(value).append(" ");
            }
        }

        builder.append("\n");
        httpRequest.set(builder);
        return joinPoint.proceed();
    }

}
