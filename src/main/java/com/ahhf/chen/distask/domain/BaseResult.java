package com.ahhf.chen.distask.domain;

import org.slf4j.helpers.MessageFormatter;

public class BaseResult<T> {

    public static final String SUCCESS_CODE = "0";

    private String             code;
    private String             message;
    private T                  result;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 格式化设置消息。从slfj借过来的格式化工具，吊炸天。 例如：
     * 
     * <pre>
     * result.setMessage(&quot;Hi {}.&quot;, &quot;there&quot;)
     * </pre>
     * 
     * @param format 消息格式
     * @param arguments 参数
     */
    public void setMessage(String format, Object... arguments) {
        if (format == null || arguments == null) {
            this.message = format;
        } else {
            this.message = MessageFormatter.arrayFormat(format, arguments).getMessage();
        }
    }

    public void setCodeSuccess() {
        this.code = SUCCESS_CODE;
    }

    public boolean success() {
        return SUCCESS_CODE.equals(code);
    }

    public boolean failed() {
        return !success();
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public void setSuccess(T result) {
        setCodeSuccess();
        this.result = result;
    }

    @Override
    public String toString() {
        return "BaseResult [code=" + code + ", message=" + message + ", result=" + result + "]";
    }

}
