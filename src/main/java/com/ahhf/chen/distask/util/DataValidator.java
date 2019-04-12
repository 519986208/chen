package com.ahhf.chen.distask.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 类DataValidator.java的实现描述：数据类型校验类，来源于网络，使用前请做好充分测试验证
 */
@SuppressWarnings("all")
public class DataValidator {

    /**
     * 是否是整数
     */
    public static boolean isInteger(String value) {
        Pattern p = null;//正则表达式  
        Matcher m = null;//操作符表达式  
        boolean b = false;
        p = p.compile("^-?[1-9]\\d*$");
        m = p.matcher(value);
        b = m.matches();
        return b;
    }

    /**
     * 是否是正整数
     */
    public static boolean isPosInteger(String value) {
        Pattern p = null;//正则表达式  
        Matcher m = null;//操作符表达式  
        boolean b = false;
        p = p.compile("^[1-9]\\d*$");
        m = p.matcher(value);
        b = m.matches();
        return b;
    }

    /**
     * 是否是负整数
     */
    public static boolean isNegInteger(String value) {
        Pattern p = null;//正则表达式  
        Matcher m = null;//操作符表达式  
        boolean b = false;
        p = p.compile("^-[1-9]\\d*$");
        m = p.matcher(value);
        b = m.matches();
        return b;
    }

    /**
     * 是否是数值
     */
    public static boolean isNum(String value) {
        Pattern p = null;//正则表达式  
        Matcher m = null;//操作符表达式  
        boolean b = false;
        p = p.compile("^([+-]?)\\d*\\.?\\d+$");
        m = p.matcher(value);
        b = m.matches();
        return b;
    }

    /**
     * 是否是正数（正整数 + 0）
     */
    public static boolean isPosNum(String value) {
        Pattern p = null;//正则表达式  
        Matcher m = null;//操作符表达式  
        boolean b = false;
        p = p.compile("^[1-9]\\d*|0$");
        m = p.matcher(value);
        b = m.matches();
        return b;
    }

    /**
     * 是否是负数（负整数 + 0）
     */
    public static boolean isNegNum(String value) {
        Pattern p = null;//正则表达式  
        Matcher m = null;//操作符表达式  
        boolean b = false;
        p = p.compile("^-[1-9]\\d*|0$");
        m = p.matcher(value);
        b = m.matches();
        return b;
    }

    /**
     * 是否是浮点数
     */
    public static boolean isDecimal(String value) {
        Pattern p = null;//正则表达式  
        Matcher m = null;//操作符表达式  
        boolean b = false;
        p = p.compile("^([+-]?)\\d*\\.\\d+$");
        m = p.matcher(value);
        b = m.matches();
        return b;
    }

    /**
     * 是否是正浮点数
     */
    public static boolean isPosDecimal(String value) {
        Pattern p = null;//正则表达式  
        Matcher m = null;//操作符表达式  
        boolean b = false;
        p = p.compile("^[1-9]\\d*.\\d*|0.\\d*[1-9]\\d*$");
        m = p.matcher(value);
        b = m.matches();
        return b;
    }

    /**
     * 是否是负浮点数
     */
    public static boolean isNegDecimal(String value) {
        Pattern p = null;//正则表达式  
        Matcher m = null;//操作符表达式  
        boolean b = false;
        p = p.compile("^-([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*)$");
        m = p.matcher(value);
        b = m.matches();
        return b;
    }

    /**
     * 是否是浮点数
     */
    public static boolean isPreciseDecmal(String value) {
        Pattern p = null;//正则表达式  
        Matcher m = null;//操作符表达式  
        boolean b = false;
        p = p.compile("^-?([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*|0?.0+|0)$");
        m = p.matcher(value);
        b = m.matches();
        return b;
    }

    /**
     * 是否是非负浮点数（正浮点数 + 0）
     */
    public static boolean isNotNegPreciseDecmal(String value) {
        Pattern p = null;//正则表达式  
        Matcher m = null;//操作符表达式  
        boolean b = false;
        p = p.compile("^[1-9]\\d*.\\d*|0.\\d*[1-9]\\d*|0?.0+|0$");
        m = p.matcher(value);
        b = m.matches();
        return b;
    }

    /**
     * 是否是非正浮点数（负浮点数 + 0）
     */
    public static boolean isNotPosPreciseDecmal(String value) {
        Pattern p = null;//正则表达式  
        Matcher m = null;//操作符表达式  
        boolean b = false;
        p = p.compile("^(-([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*))|0?.0+|0$");
        m = p.matcher(value);
        b = m.matches();
        return b;
    }

    /**
     * 是否是邮件
     */
    public static boolean isEmail(String value) {
        Pattern p = null;//正则表达式  
        Matcher m = null;//操作符表达式  
        boolean b = false;
        p = p.compile("^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$");
        m = p.matcher(value);
        b = m.matches();
        return b;
    }

    /**
     * 是否是颜色
     */
    public static boolean isColor(String value) {
        Pattern p = null;//正则表达式  
        Matcher m = null;//操作符表达式  
        boolean b = false;
        p = p.compile("^[a-fA-F0-9]{6}$");
        m = p.matcher(value);
        b = m.matches();
        return b;
    }

    /**
     * 是否是url
     */
    public static boolean isUrl(String value) {
        Pattern p = null;//正则表达式  
        Matcher m = null;//操作符表达式  
        boolean b = false;
        p = p.compile("^http[s]?:\\/\\/([\\w-]+\\.)+[\\w-]+([\\w-./?%&=]*)?$");
        m = p.matcher(value);
        b = m.matches();
        return b;
    }

    /**
     * 是否是中文
     */
    public static boolean isChinese(String value) {
        Pattern p = null;//正则表达式  
        Matcher m = null;//操作符表达式  
        boolean b = false;
        p = p.compile("^[\\u4E00-\\u9FA5\\uF900-\\uFA2D]+$");
        m = p.matcher(value);
        b = m.matches();
        return b;
    }

    /**
     * 是否是ACSII字符
     */
    public static boolean isAscii(String value) {
        Pattern p = null;//正则表达式  
        Matcher m = null;//操作符表达式  
        boolean b = false;
        p = p.compile("^[\\x00-\\xFF]+$");
        m = p.matcher(value);
        b = m.matches();
        return b;
    }

    /**
     * 是否是邮编
     */
    public static boolean isZipcode(String value) {
        Pattern p = null;//正则表达式  
        Matcher m = null;//操作符表达式  
        boolean b = false;
        p = p.compile("^\\d{6}$");
        m = p.matcher(value);
        b = m.matches();
        return b;
    }

    /**
     * 是否是手机
     */
    public static boolean isMobile(String value) {
        Pattern p = null;//正则表达式  
        Matcher m = null;//操作符表达式  
        boolean b = false;
        p = p.compile("^(13|14|15|17|18)[0-9]{9}$");
        m = p.matcher(value);
        b = m.matches();
        return b;
    }

    /**
     * 是否是ip地址
     */
    public static boolean isIp(String value) {
        Pattern p = null;//正则表达式  
        Matcher m = null;//操作符表达式  
        boolean b = false;
        p = p.compile(
                "^(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)$");
        m = p.matcher(value);
        b = m.matches();
        return b;
    }

    /**
     * 是否是图片
     */
    public static boolean isPicture(String value) {
        Pattern p = null;//正则表达式  
        Matcher m = null;//操作符表达式  
        boolean b = false;
        p = p.compile("(.*)\\.(jpg|bmp|gif|ico|pcx|jpeg|tif|png|raw|tga)$");
        m = p.matcher(value);
        b = m.matches();
        return b;
    }

    /**
     * 是否是压缩文件
     */
    public static boolean isRar(String value) {
        Pattern p = null;//正则表达式  
        Matcher m = null;//操作符表达式  
        boolean b = false;
        p = p.compile("(.*)\\.(rar|zip|7zip|tgz)$");
        m = p.matcher(value);
        b = m.matches();
        return b;
    }

    /**
     * 是否是日期
     */
    public static boolean isDate(String value) {
        Pattern p = null;//正则表达式  
        Matcher m = null;//操作符表达式  
        boolean b = false;
        p = p.compile(
                "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
        m = p.matcher(value);
        b = m.matches();
        return b;
    }

    /**
     * 是否是QQ号码
     */
    public static boolean isQq(String value) {
        Pattern p = null;//正则表达式  
        Matcher m = null;//操作符表达式  
        boolean b = false;
        p = p.compile("^[1-9]*[1-9][0-9]*$");
        m = p.matcher(value);
        b = m.matches();
        return b;
    }

    /**
     * 是否是电话号码的函数(包括验证国内区号,国际区号,分机号)
     */
    public static boolean isTel(String value) {
        Pattern p = null;//正则表达式  
        Matcher m = null;//操作符表达式  
        boolean b = false;
        p = p.compile("^(([0\\+]\\d{2,3}-)?(0\\d{2,3})-)?(\\d{7,8})(-(\\d{3,}))?$");
        m = p.matcher(value);
        b = m.matches();
        return b;
    }

    /**
     * 用来用户注册。匹配由数字、26个英文字母或者下划线组成的字符串
     */
    public static boolean isUsername(String value) {
        Pattern p = null;//正则表达式  
        Matcher m = null;//操作符表达式  
        boolean b = false;
        p = p.compile("^\\w+$");
        m = p.matcher(value);
        b = m.matches();
        return b;
    }

    /**
     * 是否是字母
     */
    public static boolean isLetter(String value) {
        Pattern p = null;//正则表达式  
        Matcher m = null;//操作符表达式  
        boolean b = false;
        p = p.compile("^[A-Za-z]+$");
        m = p.matcher(value);
        b = m.matches();
        return b;
    }

    /**
     * 是否是大写字母
     */
    public static boolean isCapitalLetter(String value) {
        Pattern p = null;//正则表达式  
        Matcher m = null;//操作符表达式  
        boolean b = false;
        p = p.compile("^[A-Z]+$");
        m = p.matcher(value);
        b = m.matches();
        return b;
    }

    /**
     * 是否是大写字母
     */
    public static boolean isLowerLetter(String value) {
        Pattern p = null;//正则表达式  
        Matcher m = null;//操作符表达式  
        boolean b = false;
        p = p.compile("^[a-z]+$");
        m = p.matcher(value);
        b = m.matches();
        return b;
    }

    /**
     * 是否是价格
     */
    public static boolean isPrice(String value) {
        Pattern p = null;//正则表达式  
        Matcher m = null;//操作符表达式  
        boolean b = false;
        p = p.compile("^([1-9]{1}[0-9]{0,}(\\.[0-9]{0,2})?|0(\\.[0-9]{0,2})?|\\.[0-9]{1,2})$");
        m = p.matcher(value);
        b = m.matches();
        return b;
    }

}
