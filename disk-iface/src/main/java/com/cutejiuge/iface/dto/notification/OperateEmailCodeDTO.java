package com.cutejiuge.iface.dto.notification;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 操作验证码参数
 *
 * @author cutejiuge
 * @since 2025/8/25 下午11:30
 */
@Data
@Builder
@Accessors(chain = true)
public class OperateEmailCodeDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 7987635144193294582L;

    /**
     * 邮箱地址
     */
    @NotBlank(message = "邮箱地址不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 验证码类型
     */
    @NotNull(message = "验证码类型不能为空")
    private Integer codeType;
}
