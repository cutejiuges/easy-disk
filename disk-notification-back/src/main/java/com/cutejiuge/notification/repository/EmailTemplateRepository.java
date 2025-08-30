package com.cutejiuge.notification.repository;

import com.cutejiuge.notification.entity.EmailTemplateEntity;
import com.cutejiuge.notification.pojo.param.QueryEmailTemplateParams;

/**
 * 邮件模板仓储层接口
 *
 * @author cutejiuge
 * @since 2025/8/26 上午8:53
 */
public interface EmailTemplateRepository {
    /**
     * 查询邮件模板
     */
    EmailTemplateEntity queryTemplate(QueryEmailTemplateParams params);
}
