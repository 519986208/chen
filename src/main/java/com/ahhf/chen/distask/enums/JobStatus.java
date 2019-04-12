package com.ahhf.chen.distask.enums;

import org.apache.commons.lang3.StringUtils;

public enum JobStatus {

    CREATE("C", "创建"),
    LOCK("L", "锁定"),
    FAIL("F", "失败"),
    SUCCESS("S", "成功");

    private String code;

    private String name;

    private JobStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static JobStatus getByCode(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        JobStatus statusRes = null;
        for (JobStatus tempE : JobStatus.values()) {
            if (tempE.getCode().equals(code)) {
                statusRes = tempE;
                break;
            }
        }
        return statusRes;
    }
}
