package com.cutejiuge.notification.service;

import com.cutejiuge.iface.dto.notification.EmailSendDTO;
import com.cutejiuge.notification.entity.EmailRecordEntity;

/**
 * 邮件服务接口
 *
 * @author cutejiuge
 * @since 2025/8/26 上午8:33
 */
public interface EmailService {
    /**
     * 发送邮件
     */
    void sendEmail(EmailSendDTO emailSendDTO);

    /**
     * 异步发送邮件
     */
    void sendEmailAsync(EmailSendDTO emailSendDTO);

    /**
     * 重试发送邮件
     */
    void retrySendEmail(EmailRecordEntity emailRecord);

    /**
     * 处理邮件发送队列
     */
    void processEmailQueue();

    /**
     * 处理邮件重试队列
     */
    void processRetryQueue();

    /**
     * 清理过期的邮件记录
     */
    void cleanExpiredRecords();
}
