package com.ahhf.chen.test.service;

import org.springframework.stereotype.Service;

@Service("studentService")
public class StudentServiceImpl implements StudentService {

    @Override
    public String study(int time) {
        try {
            Thread.sleep(2000);
            System.out.println("excute finish");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "study " + time;
    }

}
