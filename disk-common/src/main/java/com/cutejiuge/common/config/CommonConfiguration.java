package com.cutejiuge.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 通用模块自动配置类
 * @author cutejiuge
 * @since 2025/8/21 上午8:34
 */
@Slf4j
@Configuration
@ComponentScan(basePackages = {
        "com.cutejiuge.common.aspect",
        "com.cutejiuge.common.config",
        "com.cutejiuge.common.exception",
        "com.cutejiuge.common.interceptor",
        "com.cutejiuge.common.util"
})
public class CommonConfiguration {
    public CommonConfiguration() {
        log.info("disk-common模块 CommonConfiguration init success");
    }
}
