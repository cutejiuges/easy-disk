package com.cutejiuge.common.aspect;

import com.cutejiuge.common.annotation.BusinessLog;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 业务日志切面类
 *
 * @author cutejiuge
 * @since 2025/8/22 下午9:19
 */
@Slf4j
@Aspect
@Component
public class BusinessLogAspect {
    @Resource
    private ObjectMapper objectMapper;

    // 定义切点
    @Pointcut("@annotation(com.cutejiuge.common.annotation.BusinessLog)")
    public void businessLogPointcut() {}

    // 环绕切入
    @Around("businessLogPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取方法信息
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        BusinessLog businessLog = method.getAnnotation(BusinessLog.class);
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = method.getName();
        String operation = businessLog.operation();
        String operationType = businessLog.operationType();

        // 获取请求信息
        HttpServletRequest httpServletRequest = getHttpServletRequest();
        String requestUrl = httpServletRequest == null ? "" : httpServletRequest.getRequestURI();
        String requestMethod = httpServletRequest == null ? "" : httpServletRequest.getMethod();
        String traceId = getTraceId(httpServletRequest);

        // 记录请求的开始日志
        long startTime = System.currentTimeMillis();
        log.info("[业务日志] 开始执行 - 操作: {}, 类型: {}, 方法: {}.{}, URL: {} {}, TraceId: {}",
                operation, operationType, className, methodName, requestMethod, requestUrl, traceId);
        // 记录请求参数
        if (businessLog.logParams()) {
            logRequestParams(joinPoint.getArgs());
        }

        Object result = null;
        Exception exception = null;

        // 执行目标方法
        try {
            result = joinPoint.proceed();
            // 记录成功日志
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            log.info("[业务日志] 执行成功 - 操作: {}, 方法: {}.{}, 耗时: {}ms, TraceId: {}",
                    operation, className, methodName, executionTime, traceId);
            // 记录返回结果
            if (businessLog.logResult()) {
                logResult(result);
            }
            return result;
        } catch (Exception e) {
            exception = e;
            // 记录失败日志
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            log.error("[业务日志] 执行失败 - 操作: {}, 方法: {}.{}, 耗时: {}ms, 错误: {}, TraceId: {}",
                    operation, className, methodName, executionTime, e.getMessage(), traceId, e);
            throw exception;
        }
    }

    // 获取HttpServletRequest
    private HttpServletRequest getHttpServletRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attributes != null ? attributes.getRequest() : null;
        } catch (Exception e) {
            return null;
        }
    }

    // 获取链路追踪ID
    private String getTraceId(HttpServletRequest request) {
        if (request == null) {
            return "";
        }
        String traceId = request.getHeader("X-Trace-Id");
        if (traceId == null) {
            traceId = (String) request.getAttribute("traceId");
        }
        return traceId != null ? traceId : "";
    }

    // 记录请求参数
    private void logRequestParams(Object[] args) {
        if (args == null || args.length == 0) {
            return;
        }
        try {
            // 过滤敏感参数
            Object[] filteredArgs = Arrays.stream(args)
                    .map(this::filterSensitiveData)
                    .toArray();
            String paramsJson = objectMapper.writeValueAsString(filteredArgs);
            log.debug("[业务日志] 请求参数: {}", paramsJson);
        } catch (Exception e) {
            log.warn("[业务日志] 记录请求参数失败: {}", e.getMessage());
        }
    }

    // 过滤敏感数据
    private Object filterSensitiveData(Object data) {
        if (data == null) {
            return null;
        }
        // 密码、token等字段的脱敏处理
        String dataStr = data.toString();
        // 简单的敏感信息过滤
        if (dataStr.toLowerCase().contains("password")) {
            return "******";
        }
        if (dataStr.toLowerCase().contains("token")) {
            return "******";
        }
        return data;
    }

    // 记录执行结果
    private void logResult(Object result) {
        if (result == null) {
            return;
        }
        try {
            // 过滤敏感数据
            Object filteredResult = filterSensitiveData(result);
            String resultJson = objectMapper.writeValueAsString(filteredResult);
            // 限制日志长度
            if (resultJson.length() > 1000) {
                resultJson = resultJson.substring(0, 1000) + "...";
            }
            log.debug("[业务日志] 返回结果: {}", resultJson);
        } catch (Exception e) {
            log.warn("[业务日志] 记录返回结果失败: {}", e.getMessage());
        }
    }
}
