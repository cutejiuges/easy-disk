package com.cutejiuge.notification.rpc;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.cutejiuge.common.annotation.BusinessLog;
import com.cutejiuge.common.constants.NotificationConstants;
import com.cutejiuge.common.exception.BusinessException;
import com.cutejiuge.common.response.Result;
import com.cutejiuge.common.response.ResultCode;
import com.cutejiuge.common.util.EncryptUtil;
import com.cutejiuge.common.util.RedisLockUtil;
import com.cutejiuge.iface.dto.notification.EmailSendDTO;
import com.cutejiuge.iface.dto.notification.OperateEmailCodeDTO;
import com.cutejiuge.iface.dto.notification.ValidateEmailCodeDTO;
import com.cutejiuge.iface.service.notification.NotificationService;
import com.cutejiuge.notification.pojo.enums.EmailPriorityEnum;
import com.cutejiuge.notification.pojo.enums.EmailTypeEnum;
import com.cutejiuge.notification.service.EmailService;
import com.cutejiuge.notification.validation.NotificationValidation;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * RPC服务实现类
 *
 * @author cutejiuge
 * @since 2025/8/28 上午7:55
 */
@Slf4j
@Service
@DubboService(version = "1.0.0", group = "easy-disk")
public class NotificationServiceImpl implements NotificationService {
    @Resource
    private EmailService emailService;

    @Resource
    private RedisLockUtil redisLockUtil;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private NotificationValidation notificationValidation;

    @Value("${notification.email.verification-code.expire-minutes:3}")
    private Integer verificationCodeExpireMinutes;

    @Value("${notification.email.verification-code.send-interval-seconds:60}")
    private Integer sendIntervalSeconds;

    @Value("${notification.email.verification-code.max-retry-times:5}")
    private Integer maxRetryTimes;

    /**
     * 发送邮箱验证码
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @BusinessLog(operation = "发送邮箱验证码")
    public Result<Void> sendEmailVerificationCode(OperateEmailCodeDTO dto) {
        // 校验参数
        checkEmailCodeOperateParam(dto);
        // 校验发送限制
        Result<Boolean> canSendEmailCodeResult = canSendVerificationCode(dto);
        if (!canSendEmailCodeResult.isSuccess() || !canSendEmailCodeResult.getData()) {
            Result<Long> remainingTimeResult = getVerificationCodeRemainingTime(dto);
            long remainingTime = remainingTimeResult.isSuccess() ? remainingTimeResult.getData() : 0L;
            return Result.error(ResultCode.SEND_FREQUENCY_TOO_HIGH, String.format("发送过于频繁，请稍等%d秒重试", remainingTime));
        }
        // 使用分布式锁防止重复发布;
        String lockKey = NotificationConstants.SEND_VERIFICATION_CODE_LOCK_KEY_PREFIX + dto.getEmail() + dto.getCodeType();
        return redisLockUtil.executeWithLock(lockKey, 30, 60, () -> {
            try {
                executeSendVerificationCode(dto);
                log.info("邮箱验证码发送成功: email={}, codeType={}", dto.getEmail(), dto.getCodeType());
                return Result.success();
            } catch (Exception e) {
                log.error("发送邮箱验证码失败: email={}, codeType={}", dto.getEmail(), dto.getCodeType(), e);
                return Result.error(ResultCode.EMAIL_SEND_FAILED, "验证码发送失败，请稍后重试");
            }
        });
    }

    /**
     * 校验邮箱验证码
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> validateEmailVerificationCode(ValidateEmailCodeDTO dto) {
        // 校验参数
        checkEmailCodeValidateParam(dto);
        try {
            // 从缓存中获取验证码
            Map<String, Object> codeData = this.getVerificationCodeFromCache(dto);
            // 解析验证码数据
            String code = (String) codeData.get("code");
            Boolean used = (Boolean) codeData.get("used");
            // 检查验证码使用情况
            if (Boolean.TRUE.equals(used)) {
                throw new BusinessException(ResultCode.VERIFICATION_CODE_USED);
            }
            // 匹配验证码
            if (!StrUtil.equals(code, dto.getCode())) {
                throw new BusinessException(ResultCode.VERIFICATION_CODE_ERROR);
            }
            // 更新缓存中code为已使用
            useCachedCode(codeData);
            log.info("邮箱验证码验证成功: email={}, codeType={}", dto.getEmail(), dto.getCodeType());
            return Result.success(true);
        } catch (Exception e) {
            log.error("验证邮箱验证码失败: email={}, codeType={}", dto.getEmail(), dto.getCodeType(), e);
            return Result.error(ResultCode.INTERNAL_SERVER_ERROR, "验证码验证失败");
        }
    }


    /**
     * 发送邮件通知
     */
    @Override
    @BusinessLog(operation = "发送邮件通知")
    public Result<Void> sendEmailNotification(EmailSendDTO dto) {
        // 校验参数
        checkEmailSendParam(dto);
        try {
            // 设置邮件类型为通知
            dto.setEmailType(EmailTypeEnum.NOTIFICATION.getCode());
            // 发送
            this.emailService.sendEmail(dto);
            return Result.success();
        } catch (Exception e) {
            log.error("发送邮件通知失败: toEmail={}", dto.getToEmail(), e);
            return Result.error(ResultCode.EMAIL_SEND_FAILED, "邮件通知发送失败");
        }
    }

    /**
     * 异步发送邮件
     */
    @Override
    public Result<Void> sendEmailNotificationAsync(EmailSendDTO dto) {
        // 参数校验
        checkEmailSendParam(dto);
        try {
            // 设置异步发送
            dto.setAsync(true);
            // 发送
            this.emailService.sendEmailAsync(dto);
            return Result.success();
        } catch (Exception e) {
            log.error("异步发送邮件失败: toEmail={}", dto.getToEmail(), e);
            return Result.error(ResultCode.EMAIL_SEND_FAILED, "异步邮件发送失败");
        }
    }

    /**
     * 检查是否可以发送验证码
     */
    @Override
    public Result<Boolean> canSendVerificationCode(OperateEmailCodeDTO dto) {
        // 参数校验
        checkEmailCodeOperateParam(dto);
        try {
            String frequencyKey = NotificationConstants.SEND_FREQUENCY_KEY_PREFIX + dto.getEmail() + ":" + dto.getCodeType();
            boolean exists = Boolean.TRUE.equals(redisTemplate.hasKey(frequencyKey));
            return Result.success(!exists);
        } catch (Exception e) {
            log.error("检查验证码发送频率失败: email={}, codeType={}", dto.getEmail(), dto.getCodeType(), e);
            return Result.success(true); // 检查失败时允许发送
        }
    }

    /**
     * 获取验证码发送剩余时间
     */
    @Override
    public Result<Long> getVerificationCodeRemainingTime(OperateEmailCodeDTO dto) {
        // 校验参数
        checkEmailCodeOperateParam(dto);
        try {
            String frequencyKey = NotificationConstants.SEND_FREQUENCY_KEY_PREFIX + dto.getEmail() + ":" + dto.getCodeType();
            Long expireTime = redisTemplate.getExpire(frequencyKey, TimeUnit.SECONDS);
            if (ObjectUtil.isNull(expireTime) || expireTime <= 0) {
                return Result.success(0L);
            }
            return Result.success(expireTime);
        } catch (Exception e) {
            log.error("获取验证码发送剩余时间失败: email={}, codeType={}", dto.getEmail(), dto.getCodeType(), e);
            return Result.success(0L);
        }
    }

    /**
     * 使验证码失效
     */
    @Override
    public Result<Void> invalidateVerificationCode(OperateEmailCodeDTO dto) {
        // 检查参数
        checkEmailCodeOperateParam(dto);
        try {
            String codeKey = NotificationConstants.VERIFICATION_CODE_KEY_PREFIX + dto.getEmail() + ":" + dto.getCodeType();
            redisTemplate.delete(codeKey);
            log.info("验证码失效成功: email={}, codeType={}", dto.getEmail(), dto.getCodeType());
            return Result.success();
        } catch (Exception e) {
            log.error("验证码失效失败: email={}, codeType={}", dto.getEmail(), dto.getCodeType(), e);
            return Result.error(ResultCode.INTERNAL_SERVER_ERROR, "失效验证码失败");
        }
    }

    // ============================== 私有方法 ===============================
    // 校验邮件验证码的操作参数
    private void checkEmailCodeOperateParam(OperateEmailCodeDTO dto) {
        this.notificationValidation.validateEmailOperateParam(dto);
    }

    // 校验验证码校验参数
    private void checkEmailCodeValidateParam(ValidateEmailCodeDTO dto) {
        this.notificationValidation.validateEmailCodeParam(dto);
    }

    // 校验邮件发送参数
    private void checkEmailSendParam(EmailSendDTO dto) {
        this.notificationValidation.validateSendEmailParam(dto);
    }

    // 执行邮件验证码发送动作
    private void executeSendVerificationCode(OperateEmailCodeDTO dto) {
        // 检查发送频率
        Result<Boolean> canSendEmailCodeResult2 = canSendVerificationCode(dto);
        if (!canSendEmailCodeResult2.isSuccess() || !canSendEmailCodeResult2.getData()) {
            throw new BusinessException(ResultCode.SEND_FREQUENCY_TOO_HIGH, "发送过于频繁，请稍等重试");
        }
        // 失效之前的验证码
        invalidateVerificationCode(dto);
        // 生成新的验证码
        String code = EncryptUtil.generateNumericCode(6);
        // 保存验证码到redis
        cacheVerificationCode(dto, code);
        // 发送邮件验证码
        sendVerificationCode(dto, code);
        // 更新发送频率
        setVerificationFrequency(dto);
    }

    // 把验证码存储到redis
    private void cacheVerificationCode(OperateEmailCodeDTO dto, String code) {
        String codeKey = NotificationConstants.VERIFICATION_CODE_KEY_PREFIX + dto.getEmail() + ":" + dto.getCodeType();
        Map<String, Object> codeData = new HashMap<>();
        codeData.put("code", code);
        codeData.put("email", dto.getEmail());
        codeData.put("codeType", dto.getCodeType());
        codeData.put("createTime", LocalDateTime.now().toString());
        codeData.put("used", false);
        redisTemplate.opsForValue().set(codeKey, codeData, Duration.ofMinutes(verificationCodeExpireMinutes));
    }

    // 发送邮件
    private void sendVerificationCode(OperateEmailCodeDTO dto, String code) {
        // 准备发送参数
        EmailSendDTO emailSendDTO = EmailSendDTO.builder()
                .toEmail(dto.getEmail())
                .emailType(EmailTypeEnum.VERIFICATION_CODE.getCode())
                .templateCode(getTemplateCodeByType(dto.getCodeType()))
                .priority(EmailPriorityEnum.HIGH.getCode())
                .async(true)
                .build();
        // 设置模板变量
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("code", code);
        templateVariables.put("expireMinutes", verificationCodeExpireMinutes);
        emailSendDTO.setTemplateVariables(templateVariables);
        // 发送
        emailService.sendEmail(emailSendDTO);
    }

    // 设置邮件发送频率
    private void setVerificationFrequency(OperateEmailCodeDTO dto) {
        String frequencyKey = NotificationConstants.SEND_FREQUENCY_KEY_PREFIX + dto.getEmail() + ":" + dto.getCodeType();
        redisTemplate.opsForValue().set(frequencyKey, 1, Duration.ofSeconds(sendIntervalSeconds));
    }

    // 根据验证码类型获取模板编码
    private String getTemplateCodeByType(Integer codeType) {
        if (codeType == null) {
            return "REGISTER_CODE";
        }
        return switch (codeType) {
            case 1 -> "REGISTER_CODE";
            case 2 -> "LOGIN_CODE";
            case 3 -> "RESET_PASSWORD_CODE";
            default -> "REGISTER_CODE";
        };
    }

    // 从redis获取验证码
    private Map<String, Object> getVerificationCodeFromCache(ValidateEmailCodeDTO dto) {
        String cacheKey = NotificationConstants.VERIFICATION_CODE_KEY_PREFIX + dto.getEmail() + ":" + dto.getCodeType();
        String codeData = (String) redisTemplate.opsForValue().get(cacheKey);
        if (StrUtil.isBlank(codeData)) {
            throw new BusinessException(ResultCode.VERIFICATION_CODE_EXPIRE, "验证码已过期或不存在");
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> code = JSONUtil.toBean(codeData, Map.class);
        return code;
    }


    // 把缓存中的验证码更改为已经使用
    private void useCachedCode(Map<String, Object> codeData) {
        String cacheKey = NotificationConstants.VERIFICATION_CODE_KEY_PREFIX + codeData.get("email") + ":" + codeData.get("codeType");
        codeData.put("used", true);
        codeData.put("useTime", LocalDateTime.now().toString());
        redisTemplate.opsForValue().set(cacheKey, codeData, Duration.ofMinutes(verificationCodeExpireMinutes));
    }
}
