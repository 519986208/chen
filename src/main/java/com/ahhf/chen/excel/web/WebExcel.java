package com.ahhf.chen.excel.web;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ahhf.chen.excel.read.InputStreamExcelRead;
import com.ahhf.chen.excel.write.OutputStreamExcelWrite;
import com.alibaba.excel.support.ExcelTypeEnum;

/**
 * 读取web上传的excel
 */
public class WebExcel implements InputStreamExcelRead, OutputStreamExcelWrite {

    private MultipartFile       multipartFile;
    private HttpServletResponse response;

    public WebExcel(HttpServletResponse response) {
        this.response = response;
    }

    public WebExcel(MultipartFile multipartFile) {
        this.multipartFile = multipartFile;
        if (multipartFile == null) {
            throw new RuntimeException("web文件不存在！");
        }
        init(multipartFile.getOriginalFilename());
    }

    @Override
    public InputStream getInputStream() {
        InputStream inputStream = null;
        try {
            inputStream = multipartFile.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inputStream;
    }

    @Override
    public ExcelTypeEnum getExcelTypeEnum() {
        ExcelTypeEnum excelTypeEnum;
        String fileName = multipartFile.getName();
        if (fileName.toLowerCase().endsWith(".xls")) {
            excelTypeEnum = ExcelTypeEnum.XLS;
        } else {
            excelTypeEnum = ExcelTypeEnum.XLSX;
        }
        return excelTypeEnum;
    }

    @Override
    public OutputStream getOutputStream(String fileName) throws Exception {
        //创建本地文件
        String filePath = null;
        if (StringUtils.isEmpty(fileName)) {
            filePath = UUID.randomUUID().toString().replace("-", "") + ".xlsx";
        } else {
            String lowerCase = fileName.toLowerCase();
            if (lowerCase.endsWith(".xls") || lowerCase.endsWith(".xlsx")) {
                filePath = fileName;
            } else {
                filePath = fileName + ".xlsx";
            }
        }
        File dbfFile = new File(filePath);
        if (!dbfFile.exists() || dbfFile.isDirectory()) {
            dbfFile.createNewFile();
        }
        fileName = new String(filePath.getBytes(), "ISO-8859-1");
        if (!dbfFile.exists() || dbfFile.isDirectory()) {
            dbfFile.createNewFile();
        }
        response.addHeader("Content-Disposition", "filename=" + fileName);
        return response.getOutputStream();
    }

}
