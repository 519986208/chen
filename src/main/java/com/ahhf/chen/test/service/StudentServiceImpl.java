package com.ahhf.chen.test.service;

import org.springframework.stereotype.Service;

@Service("studentService")
public class StudentServiceImpl implements StudentService {

    @Override
    public String study(int time) {
        return "study " + time;
    }

}
