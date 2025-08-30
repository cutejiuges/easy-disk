package com.cutejiuge.notification.repository.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cutejiuge.notification.entity.EmailTemplateEntity;
import com.cutejiuge.notification.mapper.EmailTemplateMapper;
import com.cutejiuge.notification.pojo.param.QueryEmailTemplateParams;
import com.cutejiuge.notification.repository.EmailTemplateRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;

/**
 * 邮件模板仓储层实现类
 *
 * @author cutejiuge
 * @since 2025/8/26 上午8:54
 */
@Repository
public class EmailTemplateRepositoryImpl implements EmailTemplateRepository {
    @Resource
    private EmailTemplateMapper emailTemplateMapper;

    /**
     * 查询邮件模板
     */
    @Override
    public EmailTemplateEntity queryTemplate(QueryEmailTemplateParams params) {
        LambdaQueryWrapper<EmailTemplateEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StrUtil.isNotBlank(params.getTemplateCode()), EmailTemplateEntity::getTemplateCode, params.getTemplateCode());
        return emailTemplateMapper.selectOne(queryWrapper);
    }
}
