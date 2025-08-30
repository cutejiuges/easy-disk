package com.cutejiuge.notification.pojo.param;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 *  保存邮件发送记录参数
 * @author cutejiuge
 * @since 2025/8/27 下午11:33
*/
@Data
@Builder
@Accessors(chain = true)
public class SaveEmailRecordParams {
    /**
     * 收件人邮箱
     */
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
    private Integer emailType;

    /**
     * 邮件模板编码
     */
    private String templateCode;

    /**
     * 发送状态：0-待发送，1-发送成功，2-发送失败
     */
    private Integer sendStatus;

    /**
     * 发送时间
     */
    private LocalDateTime sendTime;

    /**
     * 失败原因
     */
    private String failReason;

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 最大重试次数
     */
    private Integer maxRetryCount;

    /**
     * 优先级：1-低，2-中，3-高
     */
    private Integer priority;
}
