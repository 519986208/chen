package com.ahhf.cly.test.function;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ahhf.chen.excel.read.ExcelRead;
import com.ahhf.chen.excel.web.WebExcel;
import com.ahhf.chen.excel.write.ExcelWrite;
import com.ahhf.cly.test.domain.PersonInfo;
import com.alibaba.fastjson.JSON;

@RestController
public class ExcelTestController {

    /**
     * 上传excel
     */
    @RequestMapping(value = "readExcel", method = RequestMethod.POST)
    public void readExcel(@RequestParam("file") MultipartFile excel) {
        ExcelRead er = new WebExcel(excel);
        List<PersonInfo> list = er.readAsObjList(PersonInfo.class);
        list.stream().forEach(p -> {
            System.out.println(JSON.toJSONString(p));
        });
    }

    /**
     * 下载excel
     */
    @RequestMapping(value = "writeExcel", method = RequestMethod.GET)
    public void writeExcel(HttpServletResponse response) throws Exception {
        //创建本地文件
        String filePath = "一个 Excel 文件.xlsx";
        ExcelWrite ew = new WebExcel(response);
        List<PersonInfo> list = getList();
        ew.writeObjList(list, filePath);
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
