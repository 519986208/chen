package com.ahhf.chen;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource("classpath:springContext.xml")
@MapperScan(basePackages = { "com.ahhf.chen.test.dao" })
public class ChenApp {

    public static void main(String[] args) {
        SpringApplication.run(ChenApp.class, args);
    }

}
