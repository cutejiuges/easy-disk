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
 * 用户Token表
 * </p>
 *
 * @author cutejiuge
 * @since 2025-08-23 23:30:47
 */
@Getter
@Setter
@TableName("tb_user_token")
public class UserTokenEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Token ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 访问令牌
     */
    @TableField("access_token")
    private String accessToken;

    /**
     * 刷新令牌
     */
    @TableField("refresh_token")
    private String refreshToken;

    /**
     * 访问令牌过期时间
     */
    @TableField("access_expire_time")
    private LocalDateTime accessExpireTime;

    /**
     * 刷新令牌过期时间
     */
    @TableField("refresh_expire_time")
    private LocalDateTime refreshExpireTime;

    /**
     * 设备ID
     */
    @TableField("device_id")
    private String deviceId;

    /**
     * 设备类型
     */
    @TableField("device_type")
    private String deviceType;

    /**
     * 状态：0-失效，1-有效
     */
    @TableField("status")
    private Integer status;

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
