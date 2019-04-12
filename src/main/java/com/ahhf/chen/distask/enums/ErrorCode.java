package com.ahhf.chen.distask.enums;

import java.text.MessageFormat;

public enum ErrorCode {

    CMN_RUNTIME_ERROR("10001", "运行出错，{0}"),
    CMN_INITIAL_ERROR("10002", "初始化出错，{0}"),
    CMN_PARAMETER_ERROR("10003", "请求参数出错，{0}"),

    CHECK_MODULE_CONFIG("20001", "Module配置校验出错，moduleCode={0}，error={1}"),
    CHECK_JOB_CONFIG("20002", "Job配置校验出错，moduleCode={0}，jobCode={1}，error={2}"),
    CHECK_Task_CONFIG("20003", "Task配置校验出错，moduleCode={0}，jobCode={1}，taskCode={2}, error={3}"),

    CHECK_JOBCODE_NOT_EMPTY("30001", "jobCode不能为空"),
    CHECK_JOBCONFIG_NOT_NULL("30002", "jobCode在配置文件中未配置"),
    CHECK_JOBPROCESS_NOT_NULL("30003", "jobCode对应的Bean处理类不存在"),

    CHECK_TASKCODE_NOT_EMPTY("30004", "taskCode不能为空"),
    CHECK_TASKCONFIG_NOT_NULL("30005", "taskCode在配置文件中未配置或者未配置taskClass"),
    CHECK_TASKPROCESS_NOT_NULL("30006", "taskCode对应的Bean处理类不存在"),
    CHECK_DEPEND_TASK_ERROR("30007", "有依赖顺序的高优先级Task执行失败"),

    SUBMIT_JOB_ERROR("40001", "提交Job出错，moduleCode={0}，jobCode={1}，error={2}"),
    SUBMIT_Task_ERROR("40002", "提交Task出错，moduleCode={0}，jobCode={1}，taskCode={2}, error={3}"),
    SAVE_JOB_ERROR("40003", "保存Job出错，moduleCode={0}，jobCode={1}，error={2}");

    private String code;
    private String pattern;

    private ErrorCode(String code, String pattern) {
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
