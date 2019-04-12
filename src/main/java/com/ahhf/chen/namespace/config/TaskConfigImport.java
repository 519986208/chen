package com.ahhf.chen.namespace.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ComponentScan(basePackages = { "com.ahhf.chen.namespace" })
//@PropertySource 会把参数加入到environment中,但是使用${xxx}取不到值
//<context:property-placeholder>和@PropertySource则相反
//@PropertySource(value = { "classpath:server.properties" })
@ImportResource(locations = { "classpath:task.xml" })
public class TaskConfigImport {

}
