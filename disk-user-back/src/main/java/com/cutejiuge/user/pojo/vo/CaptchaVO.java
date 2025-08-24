package com.cutejiuge.user.pojo.vo;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 图形验证码生成数据
 *
 * @author cutejiuge
 * @since 2025/8/24 上午10:01
 */
@Data
@Builder
@Accessors(chain = true)
public class CaptchaVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 5424331939089581379L;

    /**
     * 图形验证码ID
     */
    private String captchaId;

    /**
     * 图形验证码的图片base64编码
     */
    private String captchaImage;

    /**
     * 过期时间，单位s
     */
    private Long expiresIn;
}
