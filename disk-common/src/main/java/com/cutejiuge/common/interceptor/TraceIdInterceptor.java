package com.cutejiuge.common.interceptor;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * TraceId拦截器，实现全连路traceId透传
 *
 * @author cutejiuge
 * @since 2025/8/21 上午9:39
 */
@Slf4j
@Component
public class TraceIdInterceptor implements HandlerInterceptor {
    /**
     * TraceId请求头名称
     */
    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    /**
     * TraceId MDC键名
     */
    private static final String TRACE_ID_MDC_KEY = "traceId";

    /**
     * 前置处理
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 获取/生成traceId
        String traceId = getOrGenerateTraceId(request);
        // 设置到MDC中，用于日志输出
        MDC.put(TRACE_ID_MDC_KEY, traceId);
        // 设置到请求属性中
        request.setAttribute(TRACE_ID_MDC_KEY, traceId);
        // 设置到响应头中
        response.setHeader(TRACE_ID_HEADER, traceId);

        log.debug("traceId设置成功: {}", traceId);
        return true;
    }

    /**
     * 后置处理
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 清理MDC，释放内存
        MDC.clear();
    }

    /**
     * 从请求中获取traceId，如果获取不到的话生成新的traceId
     */
    private String getOrGenerateTraceId(HttpServletRequest request) {
        // 从请求头中取
        String traceId = request.getHeader(TRACE_ID_MDC_KEY);
        if (StrUtil.isNotBlank(traceId)) {
            return traceId;
        }

        // 从请求参数中获取
        traceId = request.getParameter("traceId");
        if (StrUtil.isNotBlank(traceId)) {
            return traceId;
        }

        // 如果没有的话，需要生成全新的traceId
        return generateTraceId();
    }

    /**
     * 生成traceId
     */
    private String generateTraceId() {
        return IdUtil.fastSimpleUUID();
    }

    /**
     * 获取当前的traceId
     */
    public static String getCurrentTraceId() {
        return MDC.get(TRACE_ID_MDC_KEY);
    }

    /**
     * 设置TraceId
     */
    public static void setTraceId(String traceId) {
        if (StrUtil.isNotBlank(traceId)) {
            MDC.put(TRACE_ID_MDC_KEY, traceId);
        }
    }

    /**
     * 清除MDC，释放空间
     */
    public static void clearCurrentTraceId() {
        MDC.remove(TRACE_ID_MDC_KEY);
    }
}
