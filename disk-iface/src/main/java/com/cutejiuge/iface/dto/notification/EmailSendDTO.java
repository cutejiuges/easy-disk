package com.cutejiuge.iface.dto.notification;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

/**
 * 邮件发送DTO
 *
 * @author cutejiuge
 * @since 2025/8/25 下午11:32
 */
@Data
@Builder
@Accessors(chain = true)
public class EmailSendDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 5072776164886699467L;

    /**
     * 收件人邮箱
     */
    @NotBlank(message = "收件人邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String toEmail;

    /**
     * 邮件主题
     */
    private String subject;

    /**
     * 邮件内容
     */
    private String content;

    /**
     * 邮件类型：1-验证码，2-通知，3-营销
     */
    @NotNull(message = "邮件类型不能为空")
    private Integer emailType;

    /**
     * 邮件模板编码
     */
    private String templateCode;

    /**
     * 模板变量
     */
    private Map<String, Object> templateVariables;

    /**
     * 优先级：1-低，2-中，3-高
     */
    private Integer priority = 1;

    /**
     * 最大重试次数
     */
    private Integer maxRetryCount = 3;

    /**
     * 是否异步发送
     */
    private Boolean async = false;

    /**
     * 验证码（用于验证码类型邮件）
     */
    private String verificationCode;

    /**
     * 验证码类型（用于验证码类型邮件）
     */
    private Integer codeType;
}
