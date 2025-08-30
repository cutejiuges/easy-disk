package com.cutejiuge.notification.validation.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.cutejiuge.common.exception.BusinessException;
import com.cutejiuge.common.response.ResultCode;
import com.cutejiuge.iface.dto.notification.EmailSendDTO;
import com.cutejiuge.iface.dto.notification.OperateEmailCodeDTO;
import com.cutejiuge.iface.dto.notification.ValidateEmailCodeDTO;
import com.cutejiuge.notification.entity.EmailRecordEntity;
import com.cutejiuge.notification.pojo.dto.OperateTemplateDTO;
import com.cutejiuge.notification.validation.NotificationValidation;
import org.springframework.stereotype.Component;

/**
 * 通知服务参数校验实现类
 *
 * @author cutejiuge
 * @since 2025/8/26 上午9:09
 */
@Component
public class NotificationValidationImpl implements NotificationValidation {
    /**
     * 校验全量的模板操作参数
     */
    @Override
    public void validateOperateTemplateParam(OperateTemplateDTO dto) {
        validateOperateTemplateParamWithoutVariables(dto);
        if (ObjectUtil.isNull(dto.getVariables())) {
            throw new BusinessException(ResultCode.PARAM_VALIDATION_ERROR, "模板变量不能为空");
        }
    }

    /**
     * 校验模板操作参数，忽略模板变量
     */
    @Override
    public void validateOperateTemplateParamWithoutVariables(OperateTemplateDTO dto) {
        if (StrUtil.isBlank(dto.getTemplateCode())) {
            throw new BusinessException(ResultCode.PARAM_VALIDATION_ERROR, "模板编码不能为空");
        }
    }

    /**
     * 校验邮件发送的参数
     */
    @Override
    public void validateSendEmailParam(EmailSendDTO dto) {
        if (ObjectUtil.isNull(dto) || StrUtil.isBlank(dto.getToEmail())) {
            throw new BusinessException(ResultCode.PARAM_VALIDATION_ERROR, "邮件发送参数不能为空");
        }
    }

    /**
     * 校验邮件发送记录
     */
    @Override
    public void validateEmailRecordParam(EmailRecordEntity emailRecord) {
        if (ObjectUtil.isNull(emailRecord)) {
            throw new BusinessException(ResultCode.PARAM_VALIDATION_ERROR, "邮件记录不能为空");
        }
    }

    /**
     * 校验邮件验证码操作参数
     */
    @Override
    public void validateEmailOperateParam(OperateEmailCodeDTO dto) {
        if (StrUtil.isBlank(dto.getEmail()) || ObjectUtil.isNull(dto.getCodeType())) {
            throw new BusinessException(ResultCode.PARAM_VALIDATION_ERROR, "邮箱和验证码类型不能为空");
        }
    }

    /**
     * 校验失效邮件验证码参数
     */
    @Override
    public void validateEmailCodeParam(ValidateEmailCodeDTO dto) {
        if (StrUtil.isBlank(dto.getEmail()) || StrUtil.isBlank(dto.getCode()) || ObjectUtil.isNull(dto.getCodeType())) {
            throw new BusinessException(ResultCode.PARAM_VALIDATION_ERROR, "参数不能为空");
        }
    }
}
