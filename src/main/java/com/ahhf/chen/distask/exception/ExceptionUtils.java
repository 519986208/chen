package com.ahhf.chen.distask.exception;

import java.util.HashMap;
import java.util.Map;

import com.ahhf.chen.distask.domain.BaseResult;
import com.ahhf.chen.distask.enums.CommonErrorCode;
import com.ahhf.chen.distask.enums.ErrorCode;

public class ExceptionUtils {

    public static BusinessException throwException(ErrorCode errCode, Object... args) {
        return new BusinessException(errCode, args);
    }

    public static BusinessException throwException(BaseResult<?> br) {
        return new BusinessException(br.getCode(), br.getMessage());
    }

    public static void setErrorInfo(BaseResult<?> br, ErrorCode errCode, Object... args) {
        br.setCode(errCode.getCode());
        br.setMessage(errCode.getErrorMsg(args));
    }

    public static void setErrorInfo(BaseResult<?> br, BaseResult<?> fbr) {
        br.setCode(fbr.getCode());
        br.setMessage(fbr.getMessage());
    }

    /**
     * 设定失败信息
     */
    public static void setErrorInfo(BaseResult<?> br, Throwable t) {
        if (t instanceof BusinessException) {
            BusinessException be = (BusinessException) t;
            br.setCode(be.getErrorCode());
            br.setMessage(be.getErrorMsg());
        } else {
            br.setCode(CommonErrorCode.SYS_RUNTIME_ERROR.getCode());
            br.setMessage(CommonErrorCode.SYS_RUNTIME_ERROR.getErrorMsg(t));
        }
    }

    /**
     * 设定成功
     */
    public static <T> void setSuccess(BaseResult<T> br, T t) {
        br.setResult(t);
        br.setCode(CommonErrorCode.SUCCESS.getCode());
    }

    /**
     * 对于单个简单参数返回封装
     */
    public static void setMapSuccess(BaseResult<Map<String, Object>> br, Object t) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("value", t);
        setSuccess(br, map);
    }

    public static <T> BaseResult<T> success(T v) {
        BaseResult<T> result = new BaseResult<T>();
        result.setResult(v);
        result.setCodeSuccess();
        return result;
    }

    public static <T> BaseResult<Map<String, Object>> successMap(T v) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("value", v);
        return success(map);
    }

    public static <T> BaseResult<T> fail(ErrorCode code, Object... objects) {
        BaseResult<T> result = new BaseResult<T>();
        result.setCode(code.getCode());
        result.setMessage(code.getErrorMsg(objects));
        return result;
    }

}
