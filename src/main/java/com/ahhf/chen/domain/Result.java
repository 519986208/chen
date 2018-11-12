package com.ahhf.chen.domain;

import lombok.Data;

/**
 * Json结果对象
 */
@Data
public class Result {

    private int    code;   // 描述代码

    private String message;// 描述信息

    private Object data;   // 提供内容封装

}
