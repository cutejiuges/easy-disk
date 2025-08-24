package com.cutejiuge.api.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户注册请求
 *
 * @author cutejiuge
 * @since 2025/8/24 上午1:40
 */
@Data
@Schema(
        description = "用户注册请求",
        requiredProperties = {"email", "password", "confirmPassword", "verificationCode", "captchaId", "captchaCode"}
)
public class RegisterRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -5646022152255662812L;

    @Schema(name = "邮箱地址", example = "user@example.com")
    @NotBlank(message = "邮箱地址不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @Schema(name = "密码", example = "123456")
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20位之间")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{6,20}$",
            message = "密码必须包含字母和数字，长度6-20位")
    private String password;

    @Schema(name = "确认密码", example = "123456")
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;

    @Schema(name = "邮箱验证码", example = "123456")
    @NotBlank(message = "邮箱验证码不能为空")
    @Size(min = 6, max = 6, message = "验证码长度必须为6位")
    private String verificationCode;

    @Schema(name = "图形验证码ID", example = "captcha-123456")
    @NotBlank(message = "图形验证码ID不能为空")
    private String captchaId;

    @Schema(name = "图形验证码", example = "ABCD")
    @NotBlank(message = "图形验证码不能为空")
    @Size(min = 4, max = 4, message = "图形验证码长度必须为4位")
    private String captchaCode;

    @Schema(name = "用户昵称", example = "用户昵称")
    @Size(max = 50, message = "用户昵称长度不能超过50个字符")
    private String nickname;
}
