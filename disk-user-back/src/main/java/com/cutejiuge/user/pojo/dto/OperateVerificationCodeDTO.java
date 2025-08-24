package com.cutejiuge.user.pojo.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 邮箱验证码操作入参
 *
 * @author cutejiuge
 * @since 2025/8/24 下午9:54
 */
@Data
@Builder
@Accessors(chain = true)
public class OperateVerificationCodeDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -396824341888019557L;

    /**
     * 邮箱验地址
     */
    private String email;

    /**
     * 邮箱验证码
     */
    private String code;

    /**
     * 验证码类型
     */
    private Integer codeType;
}
