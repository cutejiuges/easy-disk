package com.cutejiuge.iface.service.notification;

import com.cutejiuge.common.response.Result;
import com.cutejiuge.iface.dto.notification.EmailSendDTO;
import com.cutejiuge.iface.dto.notification.OperateEmailCodeDTO;
import com.cutejiuge.iface.dto.notification.ValidateEmailCodeDTO;

/**
 * 通知服务RPC接口
 *
 * @author cutejiuge
 * @since 2025-08-05 下午10：53
 */
public interface NotificationService {
    /**
     * 发送邮箱验证码
     */
    Result<Void> sendEmailVerificationCode(OperateEmailCodeDTO dto);

    /**
     * 校验邮箱验证码
     */
    Result<Boolean> validateEmailVerificationCode(ValidateEmailCodeDTO dto);

    /**
     * 发送邮件通知
     */
    Result<Void> sendEmailNotification(EmailSendDTO dto);

    /**
     * 异步发送邮件
     */
    Result<Void> sendEmailNotificationAsync(EmailSendDTO dto);

    /**
     * 检查是否可以发送验证码
     */
    Result<Boolean> canSendVerificationCode(OperateEmailCodeDTO dto);

    /**
     * 获取验证码发送剩余时间
     */
    Result<Long> getVerificationCodeRemainingTime(OperateEmailCodeDTO dto);

    /**
     * 使验证码失效
     */
    Result<Void> invalidateVerificationCode(OperateEmailCodeDTO dto);
}
