package com.ahhf.chen.distask.enums;

import org.apache.commons.lang3.StringUtils;

public enum JobTaskStatus {

    CREATE("C", "创建"),
    RUNING("R", "运行"),
    FAIL("F", "失败"),
    SUCCESS("S", "成功");

    private String code;

    private String name;

    private JobTaskStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static JobTaskStatus getByCode(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        JobTaskStatus statusRes = null;
        for (JobTaskStatus tempE : JobTaskStatus.values()) {
            if (tempE.getCode().equals(code)) {
                statusRes = tempE;
                break;
            }
        }
        return statusRes;
    }

}
