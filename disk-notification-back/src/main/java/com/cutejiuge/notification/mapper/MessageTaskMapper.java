package com.cutejiuge.notification.mapper;

import com.cutejiuge.notification.entity.MessageTaskEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 消息队列任务表 Mapper 接口
 * </p>
 *
 * @author cutejiuge
 * @since 2025-08-25 09:10:43
 */
@Mapper
public interface MessageTaskMapper extends BaseMapper<MessageTaskEntity> {

}
