package com.cutejiuge.notification.validation;

import com.cutejiuge.iface.dto.notification.EmailSendDTO;
import com.cutejiuge.iface.dto.notification.OperateEmailCodeDTO;
import com.cutejiuge.iface.dto.notification.ValidateEmailCodeDTO;
import com.cutejiuge.notification.entity.EmailRecordEntity;
import com.cutejiuge.notification.pojo.dto.OperateTemplateDTO;

/**
 * 通知服务参数校验接口
 *
 * @author cutejiuge
 * @since 2025/8/26 上午9:05
 */
public interface NotificationValidation {
    /**
     * 校验全量的模板操作参数
     */
    void validateOperateTemplateParam(OperateTemplateDTO dto);

    /**
     * 校验模板操作参数，忽略模板变量
     */
    void validateOperateTemplateParamWithoutVariables(OperateTemplateDTO dto);

    /**
     * 校验邮件发送的参数
     */
    void validateSendEmailParam(EmailSendDTO dto);

    /**
     * 校验邮件发送记录
     */
    void validateEmailRecordParam(EmailRecordEntity emailRecord);

    /**
     * 校验邮件验证码操作参数
     */
    void validateEmailOperateParam(OperateEmailCodeDTO dto);

    /**
     * 校验失效邮件验证码参数
     */
    void validateEmailCodeParam(ValidateEmailCodeDTO dto);
}
