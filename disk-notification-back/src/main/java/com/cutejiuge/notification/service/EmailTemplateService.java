package com.cutejiuge.notification.service;

import com.cutejiuge.notification.entity.EmailTemplateEntity;
import com.cutejiuge.notification.pojo.dto.OperateTemplateDTO;

/**
 * 邮件模板接口
 *
 * @author cutejiuge
 * @since 2025/8/26 上午8:41
 */
public interface EmailTemplateService {
    /**
     * 根据模板编码获取模板
     */
    EmailTemplateEntity getTemplate(OperateTemplateDTO dto);

    /**
     * 渲染邮件模板
     */
    String renderTemplate(OperateTemplateDTO dto);

    /**
     * 渲染邮件主题
     */
    String renderSubject(OperateTemplateDTO dto);

    /**
     * 验证模板变量
     */
    Boolean validateTemplateVariables(OperateTemplateDTO dto);

    /**
     * 刷新模板缓存
     */
    void refreshTemplateCache(OperateTemplateDTO dto);

    /**
     * 清空所有模板缓存
     */
    void clearAllTemplateCache();
}
