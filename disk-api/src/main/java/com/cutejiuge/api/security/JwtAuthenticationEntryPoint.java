package com.cutejiuge.api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.cutejiuge.common.response.Result;
import com.cutejiuge.common.response.ResultCode;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * JWT认证入口点 - 处理未认证的请求
 *
 * @author cutejiuge
 * @since 2025/8/24 上午12:22
 */
@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Resource
    private ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String requestURI = request.getRequestURI();
        log.warn("未认证的请求访问受保护资源: {} - {}", requestURI, authException.getMessage());
        // 设置响应状态码
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        // 构建错误响应
        Result<Void> result = Result.error(ResultCode.NOT_AUTHENTICATED);
        // 设置链路追踪ID
        String traceId = getTraceId(request);
        if (traceId != null) {
            result.traceId(traceId);
        }
        // 写入响应
        String jsonResponse = objectMapper.writeValueAsString(result);
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }

    private String getTraceId(HttpServletRequest request) {
        String traceId = request.getHeader("X-Trace-Id");
        if (traceId == null) {
            traceId = (String) request.getAttribute("traceId");
        }
        return traceId;
    }
}
