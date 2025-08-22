package com.cutejiuge.common.util;

import jakarta.servlet.http.HttpServletRequest;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * IP地址工具类
 *
 * @author cutejiuge
 * @since 2025/8/22 下午10:36
 */
@Slf4j
public class IpAddressUtil {
    // 未知IP
    private static final String UNKNOWN = "unknown";

    // 本地IP
    private static final String LOCALHOST_IP = "127.0.0.1";
    private static final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";

    /**
     * 获取客户端真实IP地址
     *
     * @param request HttpServletRequest
     * @return 客户端IP地址
     */
    public static String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return UNKNOWN;
        }
        String ip = null;
        // X-Forwarded-For：Squid 服务代理
        String ipAddresses = request.getHeader("X-Forwarded-For");
        if (StrUtil.isNotBlank(ipAddresses) && !UNKNOWN.equalsIgnoreCase(ipAddresses)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            ip = ipAddresses.split(",")[0];
        }
        // Proxy-Client-IP：apache 服务代理
        if (StrUtil.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        // WL-Proxy-Client-IP：weblogic 服务代理
        if (StrUtil.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        // HTTP_CLIENT_IP：有些代理服务器
        if (StrUtil.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        // HTTP_X_FORWARDED_FOR：有些代理服务器
        if (StrUtil.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        // X-Real-IP：nginx服务代理
        if (StrUtil.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        // 如果以上都没有获取到，则使用request.getRemoteAddr()
        if (StrUtil.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 处理本地访问
        if (LOCALHOST_IPV6.equals(ip)) {
            ip = LOCALHOST_IP;
        }
        return StrUtil.isBlank(ip) ? UNKNOWN : ip.trim();
    }

    /**
     * 获取本机IP地址
     *
     * @return 本机IP地址
     */
    public static String getLocalIp() {
        try {
            InetAddress addr = InetAddress.getLocalHost();
            return addr.getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("获取本机IP地址失败: {}", e.getMessage());
            return LOCALHOST_IP;
        }
    }

    /**
     * 获取本机主机名
     *
     * @return 本机主机名
     */
    public static String getLocalHostName() {
        try {
            InetAddress addr = InetAddress.getLocalHost();
            return addr.getHostName();
        } catch (UnknownHostException e) {
            log.warn("获取本机主机名失败: {}", e.getMessage());
            return "localhost";
        }
    }

    /**
     * 判断IP是否为内网IP
     *
     * @param ip IP地址
     * @return 是否为内网IP
     */
    public static boolean isInternalIp(String ip) {
        if (StrUtil.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            return false;
        }

        try {
            InetAddress addr = InetAddress.getByName(ip);
            return addr.isSiteLocalAddress() || addr.isLoopbackAddress();
        } catch (UnknownHostException e) {
            log.warn("判断IP是否为内网IP失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * IP地址转换为长整型
     *
     * @param ip IP地址
     * @return 长整型IP
     */
    public static long ipToLong(String ip) {
        if (StrUtil.isBlank(ip)) {
            return 0L;
        }
        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            return 0L;
        }
        try {
            long result = 0L;
            for (int i = 0; i < 4; i++) {
                int part = Integer.parseInt(parts[i]);
                if (part < 0 || part > 255) {
                    return 0L;
                }
                result = (result << 8) + part;
            }
            return result;
        } catch (NumberFormatException e) {
            log.warn("IP地址转换为长整型失败: {}", e.getMessage());
            return 0L;
        }
    }

    /**
     * 长整型转换为IP地址
     *
     * @param longIp 长整型IP
     * @return IP地址
     */
    public static String longToIp(long longIp) {
        if (longIp < 0) {
            return UNKNOWN;
        }
        return ((longIp >> 24) & 0xFF) + "." +
                ((longIp >> 16) & 0xFF) + "." +
                ((longIp >> 8) & 0xFF) + "." +
                (longIp & 0xFF);
    }

    /**
     * 验证IP地址格式
     *
     * @param ip IP地址
     * @return 是否为有效的IP地址
     */
    public static boolean isValidIp(String ip) {
        if (StrUtil.isBlank(ip)) {
            return false;
        }
        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            return false;
        }
        try {
            for (String part : parts) {
                int num = Integer.parseInt(part);
                if (num < 0 || num > 255) {
                    return false;
                }
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
