package com.cutejiuge.api.service.impl;

import com.cutejiuge.api.request.user.RegisterRequest;
import com.cutejiuge.api.response.user.CaptchaResponse;
import com.cutejiuge.api.response.user.RegisterResponse;
import com.cutejiuge.api.service.UserGatewayService;
import com.cutejiuge.common.exception.BusinessException;
import com.cutejiuge.common.response.Result;
import com.cutejiuge.user.pojo.vo.CaptchaVO;
import com.cutejiuge.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

/**
 * 用户组行为网关层实现类
 *
 * @author cutejiuge
 * @since 2025/8/24 上午2:38
 */
@Slf4j
@Service
public class UserGatewayServiceImpl implements UserGatewayService {
    @DubboReference
    private UserService userService;

    /**
     * 获取图形验证码
     */
    @Override
    public CaptchaResponse getCaptcha() {
        Result<CaptchaVO> result = userService.getCaptcha();
        if (!result.isSuccess()) {
            throw BusinessException.of(result.getCode(), result.getMessage());
        }
        CaptchaVO captchaVO = result.getData();
        log.info("getCaptcha success: {}", captchaVO);
        return CaptchaResponse.builder()
                .captchaId(captchaVO.getCaptchaId())
                .captchaImage(captchaVO.getCaptchaImage())
                .expiresIn(captchaVO.getExpiresIn())
                .build();
    }

    /**
     * 用户注册接口
     */
    @Override
    public RegisterResponse register(RegisterRequest request, HttpServletRequest httpRequest) {
        return null;
    }
}
