package com.cutejiuge.common.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * jwtToken 工具类
 *
 * @author cutejiuge
 * @since 2025/8/22 下午10:44
 */
@Slf4j
@Component
public class JwtTokenUtil {
    // JWT密钥
    @Value("${jwt.secret:easy-disk-system-jwt-secret-key-2025}")
    private String secret;

    // Access Token过期时间（秒）- 默认2小时
    @Value("${jwt.access-token-expire:7200}")
    private Long accessTokenExpire;

    // Refresh Token过期时间（秒）- 默认7天
    @Value("${jwt.refresh-token-expire:604800}")
    private Long refreshTokenExpire;

    // Token签发者
    @Value("${jwt.issuer:easy-disk-system}")
    private String issuer;

    // Token类型 - Access Token
    public static final String TOKEN_TYPE_ACCESS = "access";

    // Token类型 - Refresh Token
    public static final String TOKEN_TYPE_REFRESH = "refresh";

    // 获取签名密钥
    private SecretKey getSignKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 创建Token
     *
     * @param claims 载荷信息
     * @param subject 主题（通常是用户名）
     * @param expireTime 过期时间（秒）
     * @return JWT Token
     */
    private String createToken(Map<String, Object> claims, String subject, Long expireTime) {
        Date now = new Date();
        Date expireDate = DateUtil.offsetSecond(now, expireTime.intValue());
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuer(issuer)
                .issuedAt(now)
                .expiration(expireDate)
                .id(IdUtil.fastSimpleUUID())
                .signWith(getSignKey(), Jwts.SIG.HS256)
                .compact();
    }

    /**
     * 生成Access Token
     *
     * @param userId 用户ID
     * @param username 用户名
     * @param nickname 用户昵称
     * @param roles 用户角色
     * @return Access Token
     */
    public String generateAccessToken(Long userId, String username, String nickname, String roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("nickname", nickname);
        claims.put("roles", roles);
        claims.put("tokenType", TOKEN_TYPE_ACCESS);
        return createToken(claims, username, accessTokenExpire);
    }

    /**
     * 生成Refresh Token
     *
     * @param userId 用户ID
     * @param username 用户名
     * @return Refresh Token
     */
    public String generateRefreshToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("tokenType", TOKEN_TYPE_REFRESH);
        return createToken(claims, username, refreshTokenExpire);
    }

    /**
     * 解析Token
     *
     * @param token JWT Token
     * @return Claims
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSignKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("Token已过期: {}", e.getMessage());
            throw new RuntimeException("Token已过期");
        } catch (UnsupportedJwtException e) {
            log.warn("不支持的Token: {}", e.getMessage());
            throw new RuntimeException("不支持的Token");
        } catch (MalformedJwtException e) {
            log.warn("Token格式错误: {}", e.getMessage());
            throw new RuntimeException("Token格式错误");
        } catch (SecurityException e) {
            log.warn("Token签名验证失败: {}", e.getMessage());
            throw new RuntimeException("Token签名验证失败");
        } catch (IllegalArgumentException e) {
            log.warn("Token参数错误: {}", e.getMessage());
            throw new RuntimeException("Token参数错误");
        }
    }

    /**
     * 检查Token是否过期
     *
     * @param claims Token载荷
     * @return 是否过期
     */
    private boolean isTokenExpired(Claims claims) {
        Date expiration = claims.getExpiration();
        return expiration.before(new Date());
    }

    /**
     * 验证Token是否有效
     *
     * @param token JWT Token
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        if (StrUtil.isBlank(token)) {
            return false;
        }
        try {
            Claims claims = parseToken(token);
            return !isTokenExpired(claims);
        } catch (Exception e) {
            log.warn("Token验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 从Token中获取用户ID
     *
     * @param token JWT Token
     * @return 用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        Object userId = claims.get("userId");
        if (userId instanceof Integer) {
            return ((Integer) userId).longValue();
        }
        return (Long) userId;
    }

    /**
     * 从Token中获取用户名
     *
     * @param token JWT Token
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    /**
     * 从Token中获取用户昵称
     *
     * @param token JWT Token
     * @return 用户昵称
     */
    public String getNicknameFromToken(String token) {
        Claims claims = parseToken(token);
        return (String) claims.get("nickname");
    }

    /**
     * 从Token中获取用户角色
     *
     * @param token JWT Token
     * @return 用户角色
     */
    public String getRolesFromToken(String token) {
        Claims claims = parseToken(token);
        return (String) claims.get("roles");
    }

    /**
     * 从Token中获取Token类型
     *
     * @param token JWT Token
     * @return Token类型
     */
    public String getTokenTypeFromToken(String token) {
        Claims claims = parseToken(token);
        return (String) claims.get("tokenType");
    }

    /**
     * 从Token中获取JTI（Token ID）
     *
     * @param token JWT Token
     * @return Token ID
     */
    public String getJtiFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getId();
    }

    /**
     * 从Token中获取过期时间
     *
     * @param token JWT Token
     * @return 过期时间
     */
    public Date getExpirationFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration();
    }

    /**
     * 检查是否为Access Token
     *
     * @param token JWT Token
     * @return 是否为Access Token
     */
    public boolean isAccessToken(String token) {
        String tokenType = getTokenTypeFromToken(token);
        return TOKEN_TYPE_ACCESS.equals(tokenType);
    }

    /**
     * 检查是否为Refresh Token
     *
     * @param token JWT Token
     * @return 是否为Refresh Token
     */
    public boolean isRefreshToken(String token) {
        String tokenType = getTokenTypeFromToken(token);
        return TOKEN_TYPE_REFRESH.equals(tokenType);
    }

    /**
     * 获取Token剩余有效时间（秒）
     *
     * @param token JWT Token
     * @return 剩余有效时间
     */
    public long getTokenRemainingTime(String token) {
        Date expiration = getExpirationFromToken(token);
        long now = System.currentTimeMillis();
        return (expiration.getTime() - now) / 1000;
    }

    /**
     * 刷新Token（生成新的Access Token）
     *
     * @param refreshToken Refresh Token
     * @param username 用户名
     * @param nickname 用户昵称
     * @param roles 用户角色
     * @return 新的Access Token
     */
    public String refreshAccessToken(String refreshToken, String username, String nickname, String roles) {
        if (!validateToken(refreshToken) || !isRefreshToken(refreshToken)) {
            throw new RuntimeException("Refresh Token无效");
        }
        Long userId = getUserIdFromToken(refreshToken);
        return generateAccessToken(userId, username, nickname, roles);
    }
}
