package com.cutejiuge.iface.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 发送邮箱验证码的数据传输结构
 *
 * @author cutejiuge
 * @since 2025/8/25 下午10:54
 */
@Data
@Schema(description = "发送验证码请求数据", requiredProperties = {
        "email", "codeType", "captchaId", "captchaCode"
})
public class SendEmailCodeDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(name = "邮箱地址", example = "user@example.com")
    @NotBlank(message = "邮箱地址不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @Schema(name = "验证码类型：1-注册，2-登录，3-找回密码", example = "1")
    @NotNull(message = "验证码类型不能为空")
    private Integer codeType;

    @Schema(name = "图形验证码ID", example = "captcha-123456")
    @NotBlank(message = "图形验证码ID不能为空")
    private String captchaId;

    @Schema(name = "图形验证码", example = "ABCD")
    @NotBlank(message = "图形验证码不能为空")
    @Size(min = 4, max = 4, message = "图形验证码长度必须为4位")
    private String captchaCode;
}
