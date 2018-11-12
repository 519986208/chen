package com.ahhf.chen.utils;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

/**
 * 字符串处理工具
 */
public class StringFunction {

    /**
     * 获取去掉-的uuid
     */
    public static String getUuid() {
        return StringUtils.replace(UUID.randomUUID().toString(), "-", "").toUpperCase();
    };

    /**
     * 判断字符串是否为空
     */
    public static boolean isEmpty(String str) {
        if (str == null)
            return true;
        if (str.equals(""))
            return true;
        if (str.equalsIgnoreCase("null"))
            return true;
        if (str.trim().length() == 0)
            return true;
        return false;
    }

    public static boolean isNotEmpty(String cs) {
        return !isEmpty(cs);
    }

    /**
     * 判断传入进来的数据是否全部都是空<br>
     * 全部为空 返回true，有一项不为空则返回false
     */
    public static boolean itemsIsAllEmpty(String... items) {
        boolean flag = true;
        for (String item : items) {
            flag = flag & isEmpty(item);
        }
        return flag;
    }

}
