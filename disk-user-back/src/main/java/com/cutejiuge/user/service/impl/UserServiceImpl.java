package com.cutejiuge.user.service.impl;

import cn.hutool.core.util.IdUtil;
import com.cutejiuge.common.annotation.BusinessLog;
import com.cutejiuge.common.response.Result;
import com.cutejiuge.common.util.EncryptUtil;
import com.cutejiuge.iface.dto.user.RegisterDTO;
import com.cutejiuge.iface.vo.user.CaptchaVO;
import com.cutejiuge.iface.vo.user.RegisterVO;
import com.cutejiuge.user.repository.UserRepository;
import com.cutejiuge.user.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.util.Base64;
import java.util.Random;

/**
 * 用户服务层接口实现类
 *
 * @author cutejiuge
 * @since 2025/8/24 上午8:50
 */
@Slf4j
@Service
@DubboService
public class UserServiceImpl implements UserService {
    @Resource
    private UserRepository userRepository;
    @Resource
    private EncryptUtil encryptUtil;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 生成图形验证码
     */
    @Override
    public Result<CaptchaVO> getCaptcha() {
        // 生成图形验证码ID和内容
        String captchaId = "captcha_" + IdUtil.fastSimpleUUID();
        String captchaCode = generateCaptchaCode();
        // 生成验证码图片
        String captchaImage = generateCaptchaImage(captchaCode);
        // 缓存验证码信息
        String captchaKey = "captcha:" + captchaId;
        redisTemplate.opsForValue().set(captchaKey, captchaImage.toLowerCase(), Duration.ofMinutes(5));
        // 构建返回体
        CaptchaVO captchaVO = CaptchaVO.builder().captchaId(captchaId).captchaImage(captchaImage).expiresIn(300L).build();
        return Result.success(captchaVO);
    }

    /**
     * 用户注册接口
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @BusinessLog(operation = "用户注册")
    public Result<RegisterVO> register(RegisterDTO registerDTO) {
        String email = registerDTO.getEmail();
        // 检查邮箱是否已经存在
        return null;
    }

    // ==================== 私有方法 ========================

    // 生成验证码内容
    private String generateCaptchaCode() {
        return EncryptUtil.generateAlphanumericCode(4);
    }

    // 生成图形验证码图片
    private String generateCaptchaImage(String code) {
        int width = 120;
        int height = 40;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        // 设置背景色
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        // 设置字体
        g.setFont(new Font("Arial", Font.BOLD, 24));
        // 绘制验证码
        Random random = new Random();
        for (int i = 0; i < code.length(); i++) {
            g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            g.drawString(String.valueOf(code.charAt(i)), 20 + i * 20, 28);
        }
        // 添加干扰线
        for (int i = 0; i < 5; i++) {
            g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            g.drawLine(random.nextInt(width), random.nextInt(height),
                    random.nextInt(width), random.nextInt(height));
        }
        g.dispose();
        // 转换为Base64
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            javax.imageio.ImageIO.write(image, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
        } catch (Exception e) {
            throw new RuntimeException("生成验证码图片失败", e);
        }
    }
}
