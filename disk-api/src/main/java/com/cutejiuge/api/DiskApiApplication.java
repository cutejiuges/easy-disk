package com.cutejiuge.api;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 网关服务启动类
 *
 * @author cutejiuge
 * @since 2025/8/24 上午12:05
 */
@SpringBootApplication(
        scanBasePackages = {
                "com.cutejiuge.api",
                "com.cutejiuge.common"
        },
        exclude = {DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
@EnableDubbo
public class DiskApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(DiskApiApplication.class, args);
    }
}
