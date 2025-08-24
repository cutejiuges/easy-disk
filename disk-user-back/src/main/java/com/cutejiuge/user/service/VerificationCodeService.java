package com.cutejiuge.user.service;

import com.cutejiuge.common.response.Result;
import com.cutejiuge.user.pojo.dto.OperateVerificationCodeDTO;
import com.cutejiuge.user.pojo.dto.SendVerificationCodeDTO;

public interface VerificationCodeService {
    /**
     * 发送邮箱验证码
     */
    Result<Void> sendVerificationCode(SendVerificationCodeDTO sendVerificationCodeDTO);

    /**
     * 验证邮箱验证码
     */
    Result<Boolean> validateEmailCode(OperateVerificationCodeDTO operateVerificationCodeDTO);

    /**
     * 失效邮箱验证码
     */
    Result<Void> invalidateCode(OperateVerificationCodeDTO operateVerificationCodeDTO);

    /**
     * 是否可以发送邮箱验证码（控频率）
     */
    Result<Boolean> canSendVerificationCode(OperateVerificationCodeDTO operateVerificationCodeDTO);

    /**
     * 获取验证码发送剩余时间
     */
    Result<Long> getCodeSendRemainingTime(OperateVerificationCodeDTO operateVerificationCodeDTO);

    /**
     * 清理过期的验证码
     */
    Result<Void> clearExpiredCode(OperateVerificationCodeDTO operateVerificationCodeDTO);

    /**
     * 验证图形验证码
     */
    Result<Void> validateCaptcha(SendVerificationCodeDTO sendVerificationCodeDTO);
}
