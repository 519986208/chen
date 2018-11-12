package com.ahhf.chen;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.ahhf.chen.datasource.sequence.Sequence;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@ImportResource("classpath:springContext.xml")
@MapperScan(basePackages = { "com.ahhf.chen.test.dao" })
@Slf4j
public class ChenApp implements ApplicationRunner {

    @Value("${ahhf.dbname}")
    private String dbname;

    @Resource
    DataSource     dataSource;

    @Resource
    Sequence       personSequence;

    public static void main(String[] args) {
        SpringApplication.run(ChenApp.class, args);
    }

    @Override
    public void run(ApplicationArguments application) throws Exception {
        System.out.println("finish" + dbname);
        System.out.println("序列的下一个值： " + personSequence.nextValue());
        JdbcTemplate jt = new JdbcTemplate(dataSource);
        jt.query("select * from person ", new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet result, int arg1) throws SQLException {
                String name = result.getString("name");
                System.out.println(name);
                log.info("查询出来的： " + name);
                return name;
            }
        });
    }

}
