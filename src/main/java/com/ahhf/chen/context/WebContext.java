package com.ahhf.chen.context;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 基于springmvc web上下文环境
 */
public class WebContext {

    private WebContext() {

    }

    /**
     * 获取request
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs.getRequest();
    }

    /**
     * 获取session
     */
    public static HttpSession getSession() {
        HttpServletRequest request = getRequest();
        return request.getSession();
    }

    /**
     * 获取请求路径
     */
    public static String getRequestPath() {
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

}
