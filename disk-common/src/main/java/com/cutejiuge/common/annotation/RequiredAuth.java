package com.cutejiuge.common.annotation;

import java.lang.annotation.*;

/**
 * 权限验证注解
 *
 * @author cutejiuge
 * @since 2025/8/22 下午9:16
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiredAuth {
    /**
     * 是否需要登录
     */
    boolean requireLogin() default true;

    /**
     * 需要的角色
     */
    String[] roles() default {};

    /**
     * 需要的权限
     */
    String[] permissions() default {};

    /**
     * 权限校验模式
     * AND: 需要拥有所有权限
     * OR: 只需要拥有其中一个权限
     */
    AuthMode mode() default AuthMode.AND;

    /**
     * 权限校验模式枚举
     */
    enum AuthMode {
        AND, OR
    }
}
