package com.cutejiuge.api.security;

import cn.hutool.core.util.StrUtil;
import com.cutejiuge.api.service.UserDetailsServiceImpl;
import com.cutejiuge.common.util.JwtTokenUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT认证过滤器
 *
 * @author cutejiuge
 * @since 2025/8/24 上午12:27
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Resource
    private JwtTokenUtil jwtTokenUtil;
    @Resource
    private UserDetailsServiceImpl userDetailsService;
    // token请求头名称
    private static final String TOKEN_HEADER = "Authorization";
    // token前缀
    private static final String TOKEN_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        log.debug("JWT认证过滤器处理请求: {}", requestURI);
        try {
            // 从请求头获取token
            String token = getTokenFromRequest(request);
            if (StrUtil.isBlank(token)) {
                // 继续过滤器链
                filterChain.doFilter(request, response);
                return;
            }
            // 验证token
            if (!jwtTokenUtil.validateToken(token)) {
                log.warn("Token验证失败: {}", token.substring(0, Math.min(token.length(), 20)) + "...");
                filterChain.doFilter(request, response);
                return;
            }
            // 检查是否为Access Token
            if (!jwtTokenUtil.isAccessToken(token)) {
                log.warn("Token类型错误，期望Access Token，实际: {}", jwtTokenUtil.getTokenTypeFromToken(token));
                filterChain.doFilter(request, response);
                return;
            }
            // 从token中获取用户信息
            String username = jwtTokenUtil.getUsernameFromToken(token);
            // 如果用户名不为空且当前没有认证信息
            if (StrUtil.isNotBlank(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
                // 加载用户详情
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                // 创建认证对象
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // 设置认证信息到安全上下文
                SecurityContextHolder.getContext().setAuthentication(authentication);
                // 设置用户信息到请求属性中，供后续使用
                Long userId = jwtTokenUtil.getUserIdFromToken(token);
                request.setAttribute("currentUserId", userId);
                request.setAttribute("currentUsername", username);
                log.debug("JWT认证成功，用户: {}, ID: {}", username, userId);
            }
        } catch (Exception e) {
            log.error("JWT认证过滤器处理异常: {}", e.getMessage(), e);
            // 清除认证信息
            SecurityContextHolder.clearContext();
        }
        // 继续过滤器链
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        // 静态资源和公开接口不需要过滤
        return path.startsWith("/doc.html") ||
                path.startsWith("/knife4j/") ||
                path.equals("/favicon.ico") ||
                path.equals("/error") ||
                path.startsWith("/actuator/");
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        // 从请求头获取
        String bearerToken = request.getHeader(TOKEN_HEADER);
        if (StrUtil.isNotBlank(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        // 从请求参数获取（用于某些特殊场景，如文件下载）
        String paramToken = request.getParameter("token");
        if (StrUtil.isNotBlank(paramToken)) {
            return paramToken;
        }
        return null;
    }
}
