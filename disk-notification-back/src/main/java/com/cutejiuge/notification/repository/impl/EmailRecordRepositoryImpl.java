package com.cutejiuge.notification.repository.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cutejiuge.notification.entity.EmailRecordEntity;
import com.cutejiuge.notification.pojo.enums.EmailSendStatusEnum;
import com.cutejiuge.notification.mapper.EmailRecordMapper;
import com.cutejiuge.notification.pojo.param.QueryEmailRecordParams;
import com.cutejiuge.notification.pojo.param.SaveEmailRecordParams;
import com.cutejiuge.notification.repository.EmailRecordRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

/**
 * 邮件仓储层实现类
 *
 * @author cutejiuge
 * @since 2025/8/26 上午8:39
 */
@Repository
public class EmailRecordRepositoryImpl implements EmailRecordRepository {
    @Resource
    private EmailRecordMapper emailRecordMapper;

    /**
     * 计算满足条件的邮件发送记录数量
     */
    @Override
    public Long countEmailRecords(QueryEmailRecordParams params) {
        LambdaQueryWrapper<EmailRecordEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StrUtil.isNotBlank(params.getToEmail()), EmailRecordEntity::getToEmail, params.getToEmail())
                .ge(ObjectUtil.isNotNull(params.getStartTime()), EmailRecordEntity::getSendTime, params.getStartTime())
                .lt(ObjectUtil.isNotNull(params.getEndTime()), EmailRecordEntity::getSendTime, params.getEndTime());
        return emailRecordMapper.selectCount(wrapper);
    }

    /**
     * 保存邮件发送记录
     */
    @Override
    public int saveEmailRecord(EmailRecordEntity record) {
        return this.emailRecordMapper.insert(record);
    }

    /**
     * 更新邮件发送记录
     */
    @Override
    public int updateEmailRecord(QueryEmailRecordParams queryParams, SaveEmailRecordParams saveParams) {
        LambdaUpdateWrapper<EmailRecordEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(ObjectUtil.isNotNull(saveParams.getSendStatus()), EmailRecordEntity::getSendStatus, saveParams.getSendStatus())
                .set(ObjectUtil.isNotNull(saveParams.getSendTime()), EmailRecordEntity::getSendTime, saveParams.getSendTime())
                .set(ObjectUtil.isNotNull(saveParams.getRetryCount()), EmailRecordEntity::getRetryCount, saveParams.getRetryCount())
                .eq(ObjectUtil.isNotNull(queryParams.getId()), EmailRecordEntity::getId, queryParams.getId());
        return this.emailRecordMapper.update(wrapper);
    }

    /**
     * 查询邮件发送记录列表
     *
     * @param queryParam
     */
    @Override
    public List<EmailRecordEntity> listEmailRecords(QueryEmailRecordParams queryParam) {
        LambdaQueryWrapper<EmailRecordEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ObjectUtil.isNotNull(queryParam.getSendStatus()), EmailRecordEntity::getSendStatus, queryParam.getSendStatus());

//        // 如果需要重试，要筛选可重试的数据
//        if (Objects.equals(queryParam.getSendStatus(), EmailSendStatusEnum.FAILED.getCode()) && queryParam.getNeedRetry()) {
//            wrapper.apply("retry_count < max_retry_count");
//        }
        // 限制数量
        int size = ObjectUtil.isNotNull(queryParam.getSize()) ? queryParam.getSize() : 50; // 默认值
        IPage<EmailRecordEntity> page = new Page<>(1, size); // 第 1 页，每页 size 条
        IPage<EmailRecordEntity> resultPage = this.emailRecordMapper.selectPage(page, wrapper);
        return resultPage.getRecords();
    }

    /**
     * 删除邮件发送记录
     */
    @Override
    public int deleteEmailRecords(QueryEmailRecordParams queryParam) {
        LambdaQueryWrapper<EmailRecordEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.le(ObjectUtil.isNotNull(queryParam.getMaxCreatedAt()), EmailRecordEntity::getCreatedAt, queryParam.getMaxCreatedAt());
        return this.emailRecordMapper.delete(wrapper);
    }
}
