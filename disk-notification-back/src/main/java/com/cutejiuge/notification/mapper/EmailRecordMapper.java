package com.cutejiuge.notification.mapper;

import com.cutejiuge.notification.entity.EmailRecordEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 邮件发送记录表 Mapper 接口
 * </p>
 *
 * @author cutejiuge
 * @since 2025-08-25 09:10:42
 */
@Mapper
public interface EmailRecordMapper extends BaseMapper<EmailRecordEntity> {

}
