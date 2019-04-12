package com.ahhf.chen.distask.exception;

import com.ahhf.chen.distask.enums.ErrorCode;

public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1676031296600658465L;

    private String            errorCode;
    private String            errorMsg;

    @Override
    public String toString() {
        return String.format("%s[%s]", errorCode, errorMsg);
    }

    public BusinessException(String errorCode, String errorMsg) {
        super(errorMsg);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;

    }

    public BusinessException(ErrorCode code, Object... args) {
        this.errorCode = code.getCode();
        this.errorMsg = code.getErrorMsg(args);
    }

    public BusinessException() {
        super();
    }

    public BusinessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorMsg = message;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.errorMsg = message;
    }

    public BusinessException(String message) {
        super(message);
        this.errorMsg = message;
    }

    public BusinessException(Throwable cause) {
        super(cause);
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

}
