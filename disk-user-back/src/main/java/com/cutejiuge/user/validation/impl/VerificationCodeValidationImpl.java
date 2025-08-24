package com.cutejiuge.user.validation.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.cutejiuge.common.exception.BusinessException;
import com.cutejiuge.common.response.ResultCode;
import com.cutejiuge.user.pojo.dto.OperateVerificationCodeDTO;
import com.cutejiuge.user.pojo.dto.SendVerificationCodeDTO;
import com.cutejiuge.user.validation.VerificationCodeValidation;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 验证码相关的校验层逻辑
 *
 * @author cutejiuge
 * @since 2025/8/24 下午10:21
 */
@Component
public class VerificationCodeValidationImpl implements VerificationCodeValidation {
    /**
     * 验证邮箱验证码发送参数
     */
    @Override
    public void checkSendVerificationCodeParams(SendVerificationCodeDTO sendVerificationCodeDTO) {
        if (StrUtil.isBlank(sendVerificationCodeDTO.getCaptchaCode())) {
            throw BusinessException.of(ResultCode.CAPTCHA_CODE_ERROR, "图形验证码为空");
        }
        if (StrUtil.isBlank(sendVerificationCodeDTO.getEmail()) || ObjectUtil.isNull(sendVerificationCodeDTO.getCodeType())) {
            throw BusinessException.of(ResultCode.EMAIL_ADDRESS_INVALID, "邮箱和验证码类型不允许为空");
        }
    }

    @Override
    public void checkOperateVerificationCodeParams(OperateVerificationCodeDTO operateVerificationCodeDTO) {
        if (StrUtil.isBlank(operateVerificationCodeDTO.getEmail()) || ObjectUtil.isNull(operateVerificationCodeDTO.getCodeType())) {
            throw BusinessException.of(ResultCode.PARAM_VALIDATION_ERROR, "邮箱和验证码类型不能为空");
        }
    }
}
