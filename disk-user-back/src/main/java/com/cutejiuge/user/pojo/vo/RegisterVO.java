package com.cutejiuge.user.pojo.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户注册返回数据
 *
 * @author cutejiuge
 * @since 2025/8/24 上午8:53
 */
@Data
public class RegisterVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 6723760133989043372L;

    /**
     * 用户名
     */
    private String username;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 密码(加密后的)
     */
    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 用户状态：0-禁用，1-正常
     */
    private Integer status;

    /**
     * 用户类型：0-普通用户，1-VIP用户，2-管理员
     */
    private Integer userType;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 最后登录IP
     */
    private String lastLoginIp;

    /**
     * 总存储空间(字节)
     */
    private Long totalStorage;

    /**
     * 已使用存储空间(字节)
     */
    private Long usedStorage;
}
