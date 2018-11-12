package com.ahhf.chen.excel.read;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.compress.utils.IOUtils;

import com.ahhf.chen.excel.ExcelListener;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.metadata.BaseRowModel;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;

public interface InputStreamExcelRead extends ExcelRead {

    public abstract InputStream getInputStream();

    public abstract ExcelTypeEnum getExcelTypeEnum();

    default void init(String fileName) {
        String lowerCase = fileName.toLowerCase();
        if (!lowerCase.endsWith(".xls") && !lowerCase.endsWith(".xlsx")) {
            throw new RuntimeException("文件格式错误！");
        }
    }

    /**
     * 读取excel文件，返回List
     * 
     * @param clz clz对象必须继承BaseRowModel
     * @return
     */
    default <T extends BaseRowModel> List<T> readAsObjList(Class<T> clz) {
        ExcelListener<T> excelListener = new ExcelListener<T>();
        InputStream inputStream = null;
        try {
            inputStream = getInputStream();
            ExcelReader reader = new ExcelReader(inputStream, getExcelTypeEnum(), null, excelListener);
            List<Sheet> sheets = reader.getSheets();
            sheets.stream().forEach(sheet -> {
                sheet.setClazz(clz);
                reader.read(sheet);
            });
            return excelListener.getDatas();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                IOUtils.closeQuietly(inputStream);
            }
        }
        return null;
    }
}
