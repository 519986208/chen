package com.ahhf.cly.test.function;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Test;

import com.ahhf.chen.excel.file.FileExcel;
import com.ahhf.chen.excel.read.ExcelRead;
import com.ahhf.chen.excel.write.ExcelWrite;
import com.ahhf.cly.test.domain.PersonInfo;
import com.alibaba.fastjson.JSON;

public class TestExcelCase {

    //读取硬盘上的excel文件，转换成对象
    @Test
    public void testReadFileSystem() {
        try {
            ExcelRead er = new FileExcel("d:/home/ax啊.xlsx");
            List<PersonInfo> objs = er.readAsObjList(PersonInfo.class);
            for (PersonInfo exportInfo : objs) {
                System.out.println(JSON.toJSONString(exportInfo));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //往硬盘上写excel文件
    @Test
    public void testWriteFileSystem() {
        try {
            String fileName = "d:/home/write.xlsx";
            List<PersonInfo> list = getList();//生产随机的对象
            ExcelWrite ew = new FileExcel(fileName);
            ew.writeObjList(list, fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<PersonInfo> getList() {
        List<PersonInfo> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            PersonInfo model1 = new PersonInfo();
            model1.setName(UUID.randomUUID().toString().substring(0, 8));
            model1.setAge("10" + i);
            model1.setAddress("123456789");
            model1.setEmail("123456789@gmail.com");
            list.add(model1);
        }
        return list;
    }

}
