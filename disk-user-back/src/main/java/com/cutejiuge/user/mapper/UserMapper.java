package com.cutejiuge.user.mapper;

import com.cutejiuge.user.entity.UserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author cutejiuge
 * @since 2025-08-23 23:30:47
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {

}
