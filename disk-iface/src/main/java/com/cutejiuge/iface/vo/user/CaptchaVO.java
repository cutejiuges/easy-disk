package com.cutejiuge.iface.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 图形验证码返回数据
 *
 * @author cutejiuge
 * @since 2025/8/25 下午11:05
 */
@Data
@Builder
@Accessors(chain = true)
@Schema(name = "图形验证码响应")
public class CaptchaVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 7813172832058402321L;

    @Schema(name = "验证码Id", example = "captcha-123456")
    private String captchaId;

    @Schema(name = "验证码图片的base64编码", example = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA...")
    private String captchaImage;

    @Schema(name = "验证码过期时间(s)", example = "300")
    private Long expiresIn;
}
