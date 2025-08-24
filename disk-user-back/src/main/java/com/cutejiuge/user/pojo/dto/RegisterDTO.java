package com.cutejiuge.user.pojo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户注册请求数据
 *
 * @author cutejiuge
 * @since 2025/8/24 上午8:53
 */
@Data
@Accessors(chain = true)
public class RegisterDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -7790130767289321131L;

    /**
     * 邮箱
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20位之间")
    private String password;

    /**
     * 确认密码
     */
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;

    /**
     * 昵称
     */
    @Size(max = 50, message = "昵称长度不能超过50个字符")
    private String nickname;

    /**
     * 邮箱验证码
     */
    @NotBlank(message = "验证码不能为空")
    private String verificationCode;

    /**
     * 图形验证码ID
     */
    @NotBlank(message = "图形验证码ID不能为空")
    private String captchaId;

    /**
     * 图形验证码
     */
    @NotBlank(message = "图形验证码不能为空")
    private String captchaCode;

    /**
     * 客户端IP
     */
    private String clientIp;

    /**
     * 用户代理
     */
    private String userAgent;
}
