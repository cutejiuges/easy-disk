package com.cutejiuge.user;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 用户管理模块UserBack启动类
 */
@SpringBootApplication(scanBasePackages = {
        "com.cutejiuge.user",
        "com.cutejiuge.common"
})
@EnableDiscoveryClient
@EnableDubbo
@MapperScan("com.cutejiuge.user.mapper")
@EnableTransactionManagement
public class UserBackApplication {
    public static void main( String[] args ) {
        SpringApplication.run(UserBackApplication.class, args);
    }
}
