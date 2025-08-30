package com.cutejiuge.notification.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.cutejiuge.common.annotation.BusinessLog;
import com.cutejiuge.common.constants.NotificationConstants;
import com.cutejiuge.common.exception.BusinessException;
import com.cutejiuge.common.response.ResultCode;
import com.cutejiuge.notification.entity.EmailTemplateEntity;
import com.cutejiuge.notification.pojo.enums.TemplateStatusEnum;
import com.cutejiuge.notification.pojo.dto.OperateTemplateDTO;
import com.cutejiuge.notification.pojo.param.QueryEmailTemplateParams;
import com.cutejiuge.notification.repository.EmailTemplateRepository;
import com.cutejiuge.notification.service.EmailTemplateService;
import com.cutejiuge.notification.validation.NotificationValidation;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.Duration;
import java.util.Map;
import java.util.Set;

/**
 * 邮件模板服务实现类
 *
 * @author cutejiuge
 * @since 2025/8/26 上午8:51
 */
@Slf4j
@Service
public class EmailTemplateServiceImpl implements EmailTemplateService {
    @Resource
    private EmailTemplateRepository emailTemplateRepository;

    @Resource(name = "myTemplateEngine")
    private TemplateEngine templateEngine;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private NotificationValidation notificationValidation;

    @Value("${notification.email.template-cache-time:3600}")
    private long templateCacheTime;

    /**
     * 根据模板编码获取模板
     */
    @Override
    @BusinessLog(operation = "获取邮件模板")
    public EmailTemplateEntity getTemplate(OperateTemplateDTO dto) {
        // 校验参数
        checkOperateTemplateParamWithoutVariables(dto);
        // 从缓存中获取模板数据
        EmailTemplateEntity emailTemplate = getEmailTemplateFromCache(dto);
        if (ObjectUtil.isNotNull(emailTemplate)) {
            return emailTemplate;
        }
        // 如果缓存中没有拿到数据，从数据库中获取
        emailTemplate = getEmailTemplateFromDB(dto);
        if (ObjectUtil.isNull(emailTemplate)) {
            throw new BusinessException(ResultCode.EMAIL_TEMPLATE_NOT_FOUND, "邮件模板不存在: " + dto.getTemplateCode());
        }
        // 缓存模板
        cacheEmailTemplate(emailTemplate);
        return emailTemplate;
    }

    /**
     * 渲染邮件模板
     */
    @Override
    @BusinessLog(operation = "渲染邮件模板")
    public String renderTemplate(OperateTemplateDTO dto) {
        // 参数校验
        checkOperateTemplateParamWithoutVariables(dto);
        // 获取模板
        EmailTemplateEntity emailTemplate = getTemplate(dto);
        if (!ObjectUtil.equal(emailTemplate.getStatus(), TemplateStatusEnum.ENABLED.getCode())) {
            throw new BusinessException(ResultCode.EMAIL_TEMPLATE_DISABLED);
        }
        // 验证模板变量
        if (!validateTemplateVariables(dto)) {
            log.warn("邮件模板变量验证失败，继续渲染: {}", dto.getTemplateCode());
        }
        // 渲染模板内容
        return renderTemplateContent(emailTemplate.getContent(), dto.getVariables());
    }

    /**
     * 渲染邮件主题
     */
    @Override
    @BusinessLog(operation = "渲染邮件主题")
    public String renderSubject(OperateTemplateDTO dto) {
        // 校验参数
        checkOperateTemplateParam(dto);
        // 获取模板
        EmailTemplateEntity emailTemplate = getTemplate(dto);
        // 渲染主题
        return renderTemplateContent(emailTemplate.getSubject(), dto.getVariables());
    }

    /**
     * 验证模板变量
     */
    @Override
    public Boolean validateTemplateVariables(OperateTemplateDTO dto) {
        // 校验参数
        checkOperateTemplateParam(dto);
        // 获取模板
        EmailTemplateEntity emailTemplate = getTemplate(dto);
        // 解析模板变量
        if (StrUtil.isBlank(emailTemplate.getVariables())) {
            return true; // 没有变量定义，认为通过校验
        }
        // 检查必须变量是否全部提供了
        @SuppressWarnings("unchecked") // 注解忽略unchecked警告
        Map<String, Object> requiredVariables = JSONUtil.toBean(emailTemplate.getVariables(), Map.class);
        for (String requiredVariable : requiredVariables.keySet()) {
            if (!dto.getVariables().containsKey(requiredVariable)) {
                log.warn("缺少必需的模板变量: templateCode={}, variable={}", dto.getTemplateCode(), requiredVariable);
                return false;
            }
        }
        return true;
    }

    /**
     * 刷新模板缓存
     */
    @Override
    @BusinessLog(operation = "刷新模板缓存")
    public void refreshTemplateCache(OperateTemplateDTO dto) {
        checkOperateTemplateParamWithoutVariables(dto);
        String cacheKey = NotificationConstants.TEMPLATE_CACHE_KEY_PREFIX + dto.getTemplateCode();
        redisTemplate.delete(cacheKey);
        // 重新加载模板到缓存中
        getTemplate(dto);
        log.info("刷新缓存模板成功: {}", dto.getTemplateCode());
    }

    /**
     * 清空所有模板缓存
     */
    @Override
    public void clearAllTemplateCache() {
        Set<String> keys = redisTemplate.keys(NotificationConstants.TEMPLATE_CACHE_KEY_PREFIX + "*");
        if (ObjectUtil.isNotNull(keys) && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.info("清空所有模板缓存成功: count={}", keys.size());
        }
    }

    // ======================== 私有方法 ========================
    // 校验不带模板变量的操作参数
    private void checkOperateTemplateParamWithoutVariables(OperateTemplateDTO dto) {
        this.notificationValidation.validateOperateTemplateParamWithoutVariables(dto);
    }

    // 校验全量的操作参数
    private void checkOperateTemplateParam(OperateTemplateDTO dto) {
        this.notificationValidation.validateOperateTemplateParam(dto);
    }

    // 从缓存中获取模板
    private EmailTemplateEntity getEmailTemplateFromCache(OperateTemplateDTO dto) {
        String cacheKey = NotificationConstants.TEMPLATE_CACHE_KEY_PREFIX + dto.getTemplateCode();
        return (EmailTemplateEntity) redisTemplate.opsForValue().get(cacheKey);
    }

    // 从数据库中获取模板
    private EmailTemplateEntity getEmailTemplateFromDB(OperateTemplateDTO dto) {
        QueryEmailTemplateParams params = QueryEmailTemplateParams.builder()
                .templateCode(dto.getTemplateCode())
                .build();
        return this.emailTemplateRepository.queryTemplate(params);
    }

    // 缓存邮件模板
    private void cacheEmailTemplate(EmailTemplateEntity emailTemplate) {
        String cacheKey = NotificationConstants.TEMPLATE_CACHE_KEY_PREFIX + emailTemplate.getTemplateCode();
        redisTemplate.opsForValue().set(cacheKey, emailTemplate, Duration.ofSeconds(templateCacheTime));
    }

    // 渲染模板内容
    private String renderTemplateContent(String templateContent, Map<String, Object> variables) {
        if (StrUtil.isBlank(templateContent)) {
            return "";
        }
        try {
            // 如果模板内容包含Thymeleaf语法，使用Thymeleaf渲染
            if (templateContent.contains("${") || templateContent.contains("th:")) {
                Context context = new Context();
                if (variables != null) {
                    context.setVariables(variables);
                }
                return templateEngine.process(templateContent, context);
            } else {
                // 否则使用简单的字符串替换
                return renderSimpleTemplate(templateContent, variables);
            }
        } catch (Exception e) {
            log.warn("使用Thymeleaf渲染模板失败，尝试简单替换: {}", e.getMessage());
            return renderSimpleTemplate(templateContent, variables);
        }
    }

    // 简单模板渲染（字符串替换）
    private String renderSimpleTemplate(String templateContent, Map<String, Object> variables) {
        if (StrUtil.isBlank(templateContent) || variables == null || variables.isEmpty()) {
            return templateContent;
        }
        String result = templateContent;
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            String placeholder = "{" + entry.getKey() + "}";
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            result = result.replace(placeholder, value);
        }
        return result;
    }
}
