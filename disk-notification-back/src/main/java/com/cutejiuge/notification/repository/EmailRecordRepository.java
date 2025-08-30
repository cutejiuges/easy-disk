package com.cutejiuge.notification.repository;

import com.cutejiuge.notification.entity.EmailRecordEntity;
import com.cutejiuge.notification.pojo.param.QueryEmailRecordParams;
import com.cutejiuge.notification.pojo.param.SaveEmailRecordParams;

import java.util.List;

/**
 * 邮件仓储层接口
 *
 * @author cutejiuge
 * @since 2025/8/26 上午8:38
 */
public interface EmailRecordRepository {
    /**
     * 计算满足条件的邮件发送记录数量
     */
    Long countEmailRecords(QueryEmailRecordParams params);

    /**
     * 保存邮件发送记录
     */
    int saveEmailRecord(EmailRecordEntity record);

    /**
     * 更新邮件发送记录
     */
    int updateEmailRecord(QueryEmailRecordParams queryParams, SaveEmailRecordParams saveParams);

    /**
     * 查询邮件发送记录列表
     */
    List<EmailRecordEntity> listEmailRecords(QueryEmailRecordParams queryParam);

    /**
     * 删除邮件发送记录
     */
    int deleteEmailRecords(QueryEmailRecordParams queryParam);
}
