package com.cutejiuge.iface.dto.notification;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 验证邮箱验证码请求
 *
 * @author cutejiuge
 * @since 2025/8/25 下午11:13
 */
@Data
@Builder
@Accessors(chain = true)
public class ValidateEmailCodeDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 4223524801769688198L;

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

    /**
     * 邮箱验证码
     */
    @NotBlank(message = "邮箱验证码不允许为空")
    @Size(min = 6, max = 6, message = "请输入6位验证码")
    private String code;
}
