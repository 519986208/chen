package com.ahhf.chen.excel.write;

import java.io.OutputStream;
import java.util.List;

import org.apache.commons.compress.utils.IOUtils;
import org.springframework.util.StringUtils;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.BaseRowModel;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;

public interface OutputStreamExcelWrite extends ExcelWrite {

    public OutputStream getOutputStream(String fileName) throws Exception;

    default <T extends BaseRowModel> void writeObjList(List<T> list, String fileName) throws Exception {
        OutputStream outputStream = getOutputStream(fileName);
        try {
            ExcelWriter writer = new ExcelWriter(outputStream, getExcelTypeEnum(fileName));
            Sheet sheet = new Sheet(1, 0, list.get(0).getClass());
            writer.write(list, sheet);
            writer.finish();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                IOUtils.closeQuietly(outputStream);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("easyexcel write finished , total size : " + list.size());
    }

    default ExcelTypeEnum getExcelTypeEnum(String fileName) {
        ExcelTypeEnum excelType = null;
        if (StringUtils.isEmpty(fileName)) {
            excelType = ExcelTypeEnum.XLSX;
        } else {
            String lowerCase = fileName.toLowerCase();
            if (lowerCase.endsWith(".xls")) {
                excelType = ExcelTypeEnum.XLS;
            } else {
                excelType = ExcelTypeEnum.XLSX;
            }
        }
        return excelType;
    }

}
