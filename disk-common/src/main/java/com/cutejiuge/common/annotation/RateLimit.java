package com.cutejiuge.common.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 限流注解
 * @author cutejiuge
 * @since 2025-08-22 下午21：13
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {
    /**
     * 限流key的前缀
     */
    String prefix() default "rate_limit";

    /**
     * 限流key，支持SpEL表达式
     */
    String key() default "";

    /**
     * 时间窗口大小
     */
    int time() default 60;

    /**
     * 时间单位
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 限制次数
     */
    int count() default 100;

    /**
     * 限流类型
     */
    LimitType limitType() default LimitType.DEFAULT;

    /**
     * 限流失败消息
     */
    String message() default "访问过于频繁，请稍后再试";

    /**
     * 限流类型枚举
     */
    enum LimitType {
        /**
         * 默认策略全局限流
         */
        DEFAULT,
        /**
         * 根据请求者IP进行限流
         */
        IP,
        /**
         * 根据用户ID进行限流
         */
        USER,
        /**
         * 自定义key限流
         */
        CUSTOM
    }
}
