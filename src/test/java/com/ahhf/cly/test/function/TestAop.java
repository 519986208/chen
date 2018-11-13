package com.ahhf.cly.test.function;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ahhf.chen.ChenApp;
import com.ahhf.chen.test.service.StudentService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ChenApp.class)
public class TestAop {

    @Resource
    private StudentService stu;

    @Test
    public void stuAop() {
        try {
            System.out.println(stu.study(1));
            System.out.println(stu.study(2));
            System.out.println(stu.study(3));
            System.out.println(stu.study(4));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
