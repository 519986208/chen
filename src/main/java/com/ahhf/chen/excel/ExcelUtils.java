package com.ahhf.chen.excel;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

public class ExcelUtils {

    public static <T> List<T> read(String fileName, Class<T> clz) {
        List<T> result = new ArrayList<T>();
        EasyExcel.read(fileName, clz, new AnalysisEventListener<T>() {
            @Override
            public void invoke(T data, AnalysisContext context) {
                result.add(data);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {

            }
        }).sheet(0).doRead();
        return result;
    }

    public static void write(String fileName, Class<?> clz, List<?> list) {
        EasyExcel.write(fileName, clz).sheet(0).doWrite(list);
    }

}
