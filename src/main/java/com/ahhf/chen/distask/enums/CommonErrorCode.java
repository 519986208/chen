package com.ahhf.chen.distask.enums;

import java.text.MessageFormat;

public enum CommonErrorCode {

    SUCCESS("0", "成功."),
    SYS_MAINTAINING("40001", "系统维护中，{0}"),
    SYS_BUSY("40002", "系统繁忙，{0}"),
    SYS_RUNTIME_ERROR("41001", "系统运行出错，{0}"),
    SYS_INITIAL_ERROR("41002", "系统初始化出错，{0}");

    private String code;
    private String pattern;

    private CommonErrorCode(String code, String pattern) {
        this.code = code;
        this.pattern = pattern;
    }

    public String getCode() {
        return code;
    }

    public String getErrorMsg(Object... params) {
        String errorMsg = null;
        if ((params == null) || (params.length == 0)) {
            errorMsg = pattern;
        } else {
            MessageFormat msgFmt = new MessageFormat(pattern);
            errorMsg = msgFmt.format(params);
        }
        return errorMsg;
    }

}
