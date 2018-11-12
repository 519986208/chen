package com.ahhf.chen.excel.read;

import java.util.List;

import com.alibaba.excel.metadata.BaseRowModel;

/**
 * 类ExcelRead.java的实现描述：excel读取
 */
public interface ExcelRead {

    /**
     * @param clz 读取转换成对象的class
     * @return
     */
    public <T extends BaseRowModel> List<T> readAsObjList(Class<T> clz);

}
