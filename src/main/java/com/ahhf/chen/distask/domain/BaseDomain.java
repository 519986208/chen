package com.ahhf.chen.distask.domain;

import com.alibaba.fastjson.JSON;

public class BaseDomain {

    private Long   id;

    private String operator;  //操作人员

    private String isDeleted; //删除标志

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(String isDeleted) {
        this.isDeleted = isDeleted;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

}
