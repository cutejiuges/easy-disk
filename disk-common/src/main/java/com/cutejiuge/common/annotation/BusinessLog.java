package com.cutejiuge.common.annotation;

import java.lang.annotation.*;

/**
 * 业务日志注解
 *
 * @author cutejiuge
 * @since 2025-08-22 下午21:08
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BusinessLog {
    /**
     * 操作描述
     */
    String operation() default "";

    /**
     * 操作类型
     */
    String operationType() default "";

    /**
     * 是否记录入参
     */
    boolean logParams() default true;

    /**
     * 是否记录出参
     */
    boolean logResult() default true;

    /**
     * 是否记录执行时间
     */
    boolean logExecutionTime() default true;
}
