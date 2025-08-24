package com.cutejiuge.user.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 验证码表
 * </p>
 *
 * @author cutejiuge
 * @since 2025-08-23 23:30:47
 */
@Getter
@Setter
@TableName("tb_verification_code")
public class VerificationCodeEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 验证码ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 验证码
     */
    @TableField("code")
    private String code;

    /**
     * 验证码类型：1-注册，2-登录，3-找回密码
     */
    @TableField("code_type")
    private Integer codeType;

    /**
     * 过期时间
     */
    @TableField("expire_time")
    private LocalDateTime expireTime;

    /**
     * 是否已使用：0-未使用，1-已使用
     */
    @TableField("used")
    private Integer used;

    /**
     * 使用时间
     */
    @TableField("used_time")
    private LocalDateTime usedTime;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 删除时间
     */
    @TableField("deleted_at")
    @TableLogic
    private LocalDateTime deletedAt;
}
