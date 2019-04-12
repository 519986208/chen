package com.ahhf.chen.distask.domain.config;

public class JobTaskConstant {

    //Job日志，表示job执行结果的key
    public static final String JOB_LOG_KEY_EXEC_STATUS         = "jobSuccess";

    //Job日志，表示jobTasks处理结果的key
    public static final String JOB_LOG_KEY_TASKS_FAIL          = "jobTasksFail";

    //Task日志，表示task执行结果的key
    public static final String TASK_LOG_KEY_EXEC_STATUS        = "taskSuccess";

    //Task日志，表示task每页已经成功执行的记录数的key
    public static final String TASK_LOG_KEY_PAGE_SUCCESS_COUNT = "pageSuccessCount";
}
