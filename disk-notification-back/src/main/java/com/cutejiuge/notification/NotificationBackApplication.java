package com.cutejiuge.notification;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 通知服务启动类
 *
 * @author cutejiuge
 * @since 2025/8/25 上午7:54
 */
@SpringBootApplication(scanBasePackages = {"com.cutejiuge.common", "com.cutejiuge.notification"})
@EnableDiscoveryClient
@EnableDubbo
@EnableAsync
@EnableScheduling
@MapperScan("com.cutejiuge.notification.mapper")
public class NotificationBackApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotificationBackApplication.class, args);
    }
}
