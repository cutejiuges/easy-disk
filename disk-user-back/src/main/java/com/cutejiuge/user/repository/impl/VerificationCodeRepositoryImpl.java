package com.cutejiuge.user.repository.impl;

import com.cutejiuge.user.mapper.VerificationCodeMapper;
import com.cutejiuge.user.repository.VerificationCodeRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

/**
 * 验证码仓储层实现类
 *
 * @author cutejiuge
 * @since 2025/8/24 下午10:17
 */
@Slf4j
@Service
@DubboService
public class VerificationCodeRepositoryImpl implements VerificationCodeRepository {
    @Resource
    private VerificationCodeMapper verificationCodeMapper;
}
