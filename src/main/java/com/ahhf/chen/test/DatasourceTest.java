package com.ahhf.chen.test;

import com.alibaba.fastjson.JSON;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

public class DatasourceTest {

    public static void main(String[] args) {
        // 0
        SingleConnectionDataSource dataSource = new SingleConnectionDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://15.206.233.8:3306/rich_pay");
        dataSource.setUsername("root");
        dataSource.setPassword("BossBi@2022");
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        Object o = jdbcTemplate.queryForList("select * from pay_dictionary");
        System.out.println(JSON.toJSONString(o));
    }

}