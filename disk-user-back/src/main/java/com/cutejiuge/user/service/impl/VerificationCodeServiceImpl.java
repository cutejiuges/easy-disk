package com.cutejiuge.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.cutejiuge.common.exception.BusinessException;
import com.cutejiuge.common.response.Result;
import com.cutejiuge.common.response.ResultCode;
import com.cutejiuge.user.pojo.dto.OperateVerificationCodeDTO;
import com.cutejiuge.user.pojo.dto.SendVerificationCodeDTO;
import com.cutejiuge.user.repository.VerificationCodeRepository;
import com.cutejiuge.user.service.VerificationCodeService;
import com.cutejiuge.user.validation.VerificationCodeValidation;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 验证码服务层实现
 *
 * @author cutejiuge
 * @since 2025/8/24 下午9:37
 */
public class VerificationCodeServiceImpl implements VerificationCodeService {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private VerificationCodeRepository verificationCodeRepository;
    @Resource
    private VerificationCodeValidation verificationCodeValidation;

    @Override
    public Result<Void> sendVerificationCode(SendVerificationCodeDTO sendVerificationCodeDTO) {
        // 参数检验
        checkSendVerificationCodeParams(sendVerificationCodeDTO);
        // 校验图形验证码的有效性
        Result<Void> validateCaptchaResult = this.validateCaptcha(sendVerificationCodeDTO);
        if (!validateCaptchaResult.isSuccess()) {
            return validateCaptchaResult;
        }
        // 校验发送频率

        return null;
    }

    /**
     * 验证邮箱验证码
     */
    @Override
    public Result<Boolean> validateEmailCode(OperateVerificationCodeDTO operateVerificationCodeDTO) {
        return null;
    }

    /**
     * 失效邮箱验证码
     */
    @Override
    public Result<Void> invalidateCode(OperateVerificationCodeDTO operateVerificationCodeDTO) {
        return null;
    }

    /**
     * 是否可以发送邮箱验证码（控频率）
     */
    @Override
    public Result<Boolean> canSendVerificationCode(OperateVerificationCodeDTO operateVerificationCodeDTO) {
        // 检查操作参数
        checkOperationCodeParams(operateVerificationCodeDTO);
        // 检查是否有限频
        String frequencyKey = "verification_code:frequency:" + operateVerificationCodeDTO.getEmail() + ":" + operateVerificationCodeDTO.getCodeType();
        boolean exists = Boolean.TRUE.equals(redisTemplate.hasKey(frequencyKey));
        return Result.success(!exists);
    }

    /**
     * 获取验证码发送剩余时间
     */
    @Override
    public Result<Long> getCodeSendRemainingTime(OperateVerificationCodeDTO operateVerificationCodeDTO) {
        return null;
    }

    /**
     * 清理过期的验证码
     */
    @Override
    public Result<Void> clearExpiredCode(OperateVerificationCodeDTO operateVerificationCodeDTO) {
        return null;
    }

    /**
     * 验证图形验证码
     */
    @Override
    public Result<Void> validateCaptcha(SendVerificationCodeDTO sendVerificationCodeDTO) {
        if (StrUtil.isBlank(sendVerificationCodeDTO.getCaptchaId()) || StrUtil.isBlank(sendVerificationCodeDTO.getCaptchaCode())) {
            return Result.error(ResultCode.CAPTCHA_CODE_ERROR, "验证码不能为空");
        }
        String cacheKey = "captcha:" + sendVerificationCodeDTO.getCaptchaId();
        String cachedCode = (String) redisTemplate.opsForValue().get(cacheKey);
        if (StrUtil.isBlank(cachedCode)) {
            return Result.error(ResultCode.CAPTCHA_CODE_ERROR, "验证码已过期");
        }
        if (!sendVerificationCodeDTO.getCaptchaCode().equalsIgnoreCase(cachedCode)) {
            return Result.error(ResultCode.CAPTCHA_CODE_ERROR);
        }
        // 验证成功后删除验证码
        redisTemplate.delete(cacheKey);
        return Result.success();
    }

    // ===================== 私有方法 ===========================
    // 检查验证法发送参数
    private void checkSendVerificationCodeParams(SendVerificationCodeDTO sendVerificationCodeDTO) {
        this.verificationCodeValidation.checkSendVerificationCodeParams(sendVerificationCodeDTO);
    }

    // 检查验证操作参数
    private void checkOperationCodeParams(OperateVerificationCodeDTO operateVerificationCodeDTO) {
        this.verificationCodeValidation.checkOperateVerificationCodeParams(operateVerificationCodeDTO);
    }
}
