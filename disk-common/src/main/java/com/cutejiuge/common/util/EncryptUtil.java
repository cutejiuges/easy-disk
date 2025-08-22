package com.cutejiuge.common.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.crypto.symmetric.AES;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 加密工具类
 *
 * @author cutejiuge
 * @since 2025/8/22 下午9:37
 */
@Slf4j
@Component
public class EncryptUtil {
    // AES加密密钥
    @Value("${encrypt.aes-key:easy-disk-system-aes-key-2025}")
    private String aesKey;

    // 密码加密盐值
    @Value("${encrypt.password-salt:easy-disk-system-salt-2025}")
    private String passwordSalt;

    // AES加密器
    private AES aes;
    @PostConstruct
    public void init() {
        // 确保AES密钥长度为16字节
        String key = StrUtil.fillAfter(aesKey, '0', 16).substring(0, 16);
        this.aes = new AES(key.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * MD5 加密
     * @param data 待加密数据
     * @return md5加密结果
     */
    public static String md5(String data) {
        if (StrUtil.isBlank(data)) {
            return null;
        }
        return DigestUtil.md5Hex(data);
    }

    /**
     * MD5加盐加密
     * @param data 待加密数据
     * @param salt 盐值
     * @return md5加密结果
     */
    public static String md5WithSalt(String data, String salt) {
        if (StrUtil.isBlank(data)) {
            return null;
        }
        return DigestUtil.md5Hex(data + salt);
    }

    /**
     * sha256 加密
     * @param data 待加密数据
     * @return sha256加密结果
     */
    public static String sha256(String data) {
        if (StrUtil.isBlank(data)) {
            return null;
        }
        return DigestUtil.sha256Hex(data);
    }

    /**
     * sha256加盐加密
     * @param data 待加密数据
     * @param salt 盐值
     * @return sha256加密结果
     */
    public static String sha256WithSalt(String data, String salt) {
        if (StrUtil.isBlank(data)) {
            return null;
        }
        return DigestUtil.sha256Hex(data + salt);
    }

    /**
     * 密码加密（使用配置的盐值）
     *
     * @param password 原始密码
     * @return 加密后的密码
     */
    public String encryptPassword(String password) {
        if (StrUtil.isBlank(password)) {
            return null;
        }
        return sha256WithSalt(password, passwordSalt);
    }

    /**
     * 验证密码
     *
     * @param rawPassword 原始密码
     * @param encryptedPassword 加密后的密码
     * @return 是否匹配
     */
    public boolean verifyPassword(String rawPassword, String encryptedPassword) {
        if (StrUtil.isBlank(rawPassword) || StrUtil.isBlank(encryptedPassword)) {
            return false;
        }
        String encrypted = encryptPassword(rawPassword);
        return encryptedPassword.equals(encrypted);
    }

    /**
     * AES加密
     *
     * @param data 原始数据
     * @return 加密后的数据（Base64编码）
     */
    public String aesEncrypt(String data) {
        if (StrUtil.isBlank(data)) {
            return null;
        }
        try {
            byte[] encrypted = aes.encrypt(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            log.error("AES加密失败", e);
            throw new RuntimeException("AES加密失败", e);
        }
    }

    /**
     * AES解密
     *
     * @param encryptedData 加密后的数据（Base64编码）
     * @return 原始数据
     */
    public String aesDecrypt(String encryptedData) {
        if (StrUtil.isBlank(encryptedData)) {
            return null;
        }
        try {
            byte[] encrypted = Base64.getDecoder().decode(encryptedData);
            byte[] decrypted = aes.decrypt(encrypted);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("AES解密失败", e);
            throw new RuntimeException("AES解密失败", e);
        }
    }

    /**
     * 生成随机盐值
     *
     * @param length 盐值长度
     * @return 随机盐值
     */
    public static String generateSalt(int length) {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[length];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * 生成随机密码
     *
     * @param length 密码长度
     * @return 随机密码
     */
    public static String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }

        return password.toString();
    }

    /**
     * 生成数字验证码
     *
     * @param length 验证码长度
     * @return 数字验证码
     */
    public static String generateNumericCode(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < length; i++) {
            code.append(random.nextInt(10));
        }

        return code.toString();
    }

    /**
     * 生成字母数字验证码
     *
     * @param length 验证码长度
     * @return 字母数字验证码
     */
    public static String generateAlphanumericCode(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < length; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }

        return code.toString();
    }

    /**
     * 计算文件MD5
     *
     * @param fileBytes 文件字节数组
     * @return 文件MD5值
     */
    public static String calculateFileMd5(byte[] fileBytes) {
        if (fileBytes == null || fileBytes.length == 0) {
            return null;
        }
        return DigestUtil.md5Hex(fileBytes);
    }

    /**
     * 验证文件MD5
     *
     * @param fileBytes 文件字节数组
     * @param expectedMd5 期望的MD5值
     * @return 是否匹配
     */
    public static boolean verifyFileMd5(byte[] fileBytes, String expectedMd5) {
        if (fileBytes == null || StrUtil.isBlank(expectedMd5)) {
            return false;
        }
        String actualMd5 = calculateFileMd5(fileBytes);
        return expectedMd5.equalsIgnoreCase(actualMd5);
    }

    /**
     * 计算文件sha256
     *
     * @param fileBytes 文件字节数组
     * @return 文件sha256值
     */
    public static String calculateFileSha256(byte[] fileBytes) {
        if (fileBytes == null || fileBytes.length == 0) {
            return null;
        }
        return DigestUtil.sha256Hex(fileBytes);
    }

    /**
     * 验证文件sha256
     *
     * @param fileBytes 文件字节数组
     * @param expectedSha256 期望的sha256值
     * @return 是否匹配
     */
    public static boolean verifyFileSha256(byte[] fileBytes, String expectedSha256) {
        if (fileBytes == null || StrUtil.isBlank(expectedSha256)) {
            return false;
        }
        String actualSha256 = calculateFileSha256(fileBytes);
        return expectedSha256.equalsIgnoreCase(actualSha256);
    }
}
