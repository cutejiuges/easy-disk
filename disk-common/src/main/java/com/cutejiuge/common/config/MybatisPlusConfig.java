package com.cutejiuge.common.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.LocalDateTime;

/**
 * mybatis-plus的配置类
 * @author cutejiuge
 * @since 2025/8/21 上午8:43
 */
@Slf4j
@Configuration
@EnableTransactionManagement
public class MybatisPlusConfig {

    /**
     * mybatis-plus拦截器配置
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 分页插件
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        // 设置请求页面大于最大页面的操作，true调回到首页，false继续操作
        paginationInnerInterceptor.setOverflow(false);
        // 设置分页的最大限制，设置为500条
        paginationInnerInterceptor.setMaxLimit(500L);
        interceptor.addInnerInterceptor(paginationInnerInterceptor);
        log.info("disk-common: MybatisPlusInterceptor配置完成");
        return interceptor;
    }

    /**
     * 自定义填充配置
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MyMetaObjectHandler();
    }

    /**
     * 实现自定义元对象处理器
     */
    public static class MyMetaObjectHandler implements MetaObjectHandler {

        /**
         * 插入时自动填充
         */
        @Override
        public void insertFill(MetaObject metaObject) {
            log.debug("插入时自动填充...");
            // 填充创建时间和更新时间
            this.strictInsertFill(metaObject, "createdAt", LocalDateTime.class, LocalDateTime.now());
            this.strictInsertFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
        }

        /**
         * 更新时自动填充
         */
        @Override
        public void updateFill(MetaObject metaObject) {
            log.debug("更新时自动填充...");
            // 填充更新时间
            this.strictUpdateFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
        }
    }
}
