package com.cutejiuge.api.controller;

import com.cutejiuge.api.request.user.RegisterRequest;
import com.cutejiuge.api.response.user.CaptchaResponse;
import com.cutejiuge.api.response.user.RegisterResponse;
import com.cutejiuge.api.service.UserGatewayService;
import com.cutejiuge.common.annotation.BusinessLog;
import com.cutejiuge.common.annotation.RateLimit;
import com.cutejiuge.common.response.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户相关接口controller
 *
 * @author cutejiuge
 * @since 2025/8/24 上午2:31
 */
@Slf4j
@RestController
@RequestMapping("/user")
@Tag(name = "用户管理")
@Validated
public class UserController {
    @Resource
    private UserGatewayService userGatewayService;

    @GetMapping("/captcha")
    @Operation(description = "获取图形验证码")
    @RateLimit(count = 20, time = 60, message = "获取验证码过于频繁，请稍候再试")
    public Result<CaptchaResponse> getCaptcha() {
        log.info("获取图形验证码请求");
        CaptchaResponse captchaResponse = userGatewayService.getCaptcha();
        return Result.success("获取图形验证码成功", captchaResponse);
    }

    @PostMapping("/register")
    @Operation(description = "用户注册接口")
    @BusinessLog(operation = "用户注册", operationType = "REGISTER")
    @RateLimit(key = "registerRequest.email", count = 5, time = 300, message = "注册请求过于频繁，请稍候再试")
    public Result<RegisterResponse> register(@Valid @RequestBody RegisterRequest registerRequest, HttpServletRequest httpRequest) {
        log.info("用户注册请求: {}", registerRequest.getEmail());
        RegisterResponse response = userGatewayService.register(registerRequest, httpRequest);
        return Result.success("注册成功", response);
    }
}
