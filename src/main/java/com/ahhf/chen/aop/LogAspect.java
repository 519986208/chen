package com.ahhf.chen.aop;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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

    private static final String LINE = "\n";

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @Pointcut("execution(public * com.ahhf.chen.test.controller.*Controller.*(..))")
    public void pointCutMethod() {
        log.info("pointCutMethod");
    }

    @Around("pointCutMethod()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Long startTime = System.currentTimeMillis();
        String requestPath = this.getRequestPath();
        String method = this.getRequest().getMethod();
        String targetName = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] arguments = joinPoint.getArgs();
        StringBuilder builder = new StringBuilder();
        builder.append(LINE);
        builder.append("请求路径：" + requestPath).append(LINE);
        builder.append("http请求方式：" + method).append(LINE);
        builder.append("类名：" + targetName).append(LINE);
        builder.append("方法名：" + methodName).append(LINE);
        builder.append("参数：");
        int length = arguments.length;
        for (int i = 0; i < length; i++) {
            Object obj = arguments[i];
            if (this.isNeedExclude(obj)) {
                continue;
            }
            try {
                String value = JSON.toJSONString(obj);
                if (i == length - 1) {
                    builder.append(value);
                } else {
                    builder.append(value).append(" ");
                }
            } catch (Exception e) {
                log.warn("记录日志的json序列化异常 " + obj);
            }
        }
        builder.append(LINE);

        Object returnValue = joinPoint.proceed();
        builder.append("开始时间：").append(SIMPLE_DATE_FORMAT.format(new Date(startTime))).append(LINE);
        long currentTimeMillis = System.currentTimeMillis();
        builder.append("结束时间：").append(SIMPLE_DATE_FORMAT.format(new Date(currentTimeMillis))).append(LINE);
        builder.append("总共耗时： ").append(currentTimeMillis - startTime).append("毫秒").append(LINE);
        builder.append("方法返回值：");
        builder.append(JSON.toJSONString(returnValue));
        builder.append(LINE);
        log.info(builder.toString());
        return returnValue;
    }

    /**
     * 获取request
     */
    private HttpServletRequest getRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs.getRequest();
    }

    /**
     * 获取请求路径
     */
    private String getRequestPath() {
        HttpServletRequest httpServletRequest = getRequest();
        StringBuilder builder = new StringBuilder();
        String scheme = httpServletRequest.getScheme();
        String serverName = httpServletRequest.getServerName();
        int serverPort = httpServletRequest.getServerPort();
        String requestURI = httpServletRequest.getRequestURI();
        builder.append(scheme);
        builder.append("://");
        builder.append(serverName);
        builder.append(":");
        builder.append(serverPort);
        builder.append(requestURI);

        Enumeration<String> parameterNames = httpServletRequest.getParameterNames();
        boolean hasMoreElements = parameterNames.hasMoreElements();
        if (hasMoreElements) {
            builder.append("?");
        }
        while (parameterNames.hasMoreElements()) {
            String element = parameterNames.nextElement();
            builder.append(element);
            builder.append("=");
            builder.append(httpServletRequest.getParameter(element));
            builder.append("&");
        }
        String str = builder.toString();
        if (hasMoreElements) {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    private boolean isNeedExclude(Object obj) {
        if (obj == null) {
            return true;
        }
        if (obj instanceof HttpServletRequest || obj instanceof BindingResult || obj instanceof HttpServletResponse
                || obj instanceof HttpSession) {
            return true;
        }
        return false;
    }

}
