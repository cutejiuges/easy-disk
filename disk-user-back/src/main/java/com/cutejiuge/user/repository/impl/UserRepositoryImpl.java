package com.cutejiuge.user.repository.impl;

import com.cutejiuge.user.mapper.UserMapper;
import com.cutejiuge.user.repository.UserRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

/**
 * 处理User表的数据操作
 *
 * @author cutejiuge
 * @since 2025/8/24 上午9:34
 */
@Slf4j
@Repository
public class UserRepositoryImpl implements UserRepository {
    @Resource
    private UserMapper userMapper;
}
