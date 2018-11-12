package com.ahhf.chen.excel.write;

import java.util.List;

import com.alibaba.excel.metadata.BaseRowModel;

/**
 * 类ExcelWrite.java的实现描述：excel读取
 */
public interface ExcelWrite {

    /**
     * @param list 要写到excel中的集合对象
     * @param fileName 生成的excel文件名称
     * @throws Exception
     */
    public <T extends BaseRowModel> void writeObjList(List<T> list, String fileName) throws Exception;

}
