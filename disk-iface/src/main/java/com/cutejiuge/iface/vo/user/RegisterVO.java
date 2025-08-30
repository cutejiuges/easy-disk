package com.cutejiuge.iface.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户注册响应
 *
 * @author cutejiuge
 * @since 2025/8/25 下午11:06
 */
@Data
@Schema(description = "用户注册响应")
public class RegisterVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 8265548867529922887L;

    @Schema(name = "用户ID", example = "1001")
    private Long userId;

    @Schema(name = "用户名", example = "user@example.com")
    private String username;

    @Schema(name = "用户昵称", example = "用户昵称")
    private String nickname;

    @Schema(name = "注册时间", example = "2025-01-01 12:00:00")
    private String registerTime;
}
