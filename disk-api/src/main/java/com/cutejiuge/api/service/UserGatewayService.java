package com.cutejiuge.api.service;

import com.cutejiuge.api.request.user.RegisterRequest;
import com.cutejiuge.api.response.user.CaptchaResponse;
import com.cutejiuge.api.response.user.RegisterResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface UserGatewayService {
    /**
     * 获取图形验证码
     */
    CaptchaResponse getCaptcha();
    /**
     * 用户注册接口
     */
    RegisterResponse register(RegisterRequest request, HttpServletRequest httpRequest);
}
