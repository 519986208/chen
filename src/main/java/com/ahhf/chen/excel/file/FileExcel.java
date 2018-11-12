package com.ahhf.chen.excel.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.springframework.util.StringUtils;

import com.ahhf.chen.excel.read.InputStreamExcelRead;
import com.ahhf.chen.excel.write.OutputStreamExcelWrite;
import com.alibaba.excel.support.ExcelTypeEnum;

/**
 * 读取本地文件excel
 */
public class FileExcel implements InputStreamExcelRead, OutputStreamExcelWrite {

    private String fileName = null;

    public FileExcel(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            throw new IllegalArgumentException("文件名称不能为空");
        }
        this.fileName = fileName;
        File file = new File(fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        init(fileName);
    }

    @Override
    public InputStream getInputStream() {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return inputStream;
    }

    @Override
    public ExcelTypeEnum getExcelTypeEnum() {
        ExcelTypeEnum excelTypeEnum;
        if (fileName.toLowerCase().endsWith(".xls")) {
            excelTypeEnum = ExcelTypeEnum.XLS;
        } else {
            excelTypeEnum = ExcelTypeEnum.XLSX;
        }
        return excelTypeEnum;
    }

    @Override
    public OutputStream getOutputStream(String fileName) throws Exception {
        return new FileOutputStream(fileName);
    }

}
