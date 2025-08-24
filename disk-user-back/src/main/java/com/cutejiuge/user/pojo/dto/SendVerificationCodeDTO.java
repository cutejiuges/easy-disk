package com.cutejiuge.user.pojo.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 发送邮箱验证码请求结构
 *
 * @author cutejiuge
 * @since 2025/8/24 下午9:28
 */
@Data
@Builder
@Accessors(chain = true)
public class SendVerificationCodeDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 8828667164880895002L;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 验证码类型, 1-注册 2-登陆 3-找回密码
     */
    private Integer codeType;

    /**
     * 图形验证码id
     */
    private String captchaId;

    /**
     * 图形验证码
     */
    private String captchaCode;
}
