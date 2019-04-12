package com.ahhf.chen.distask.domain;

import lombok.Data;

@Data
public class JobResult {

    private boolean success = false;

    /**
     * 执行失败的简要错误信息
     */
    private String  errorMsg;

    /**
     * 执行失败异常堆栈信息
     */
    private String  errStack;

    public JobResult() {
        super();
    }

    public JobResult(boolean success) {
        super();
        this.success = success;
    }

    public JobResult(String errorMsg) {
        super();
        this.errorMsg = errorMsg;
    }

    public JobResult(String errorMsg, String errStack) {
        super();
        this.errorMsg = errorMsg;
        this.errStack = errStack;
    }

}
