package com.ahhf.chen.test.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ahhf.chen.datasource.sequence.Sequence;
import com.ahhf.chen.domain.Result;

@RestController
public class TestController {

    @Resource
    private Sequence personSequence;

    @GetMapping("justTest")
    public Object fdafd() {
        Result r = new Result();
        r.setCode(200);
        r.setData(personSequence.nextValue());
        return r;
    }

}
