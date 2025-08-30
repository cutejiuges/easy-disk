package com.cutejiuge.user.service;

import com.cutejiuge.common.response.Result;
import com.cutejiuge.iface.dto.user.RegisterDTO;
import com.cutejiuge.iface.vo.user.CaptchaVO;
import com.cutejiuge.iface.vo.user.RegisterVO;

/**
 * 用户服务层接口
 *
 * @author cutejiuge
 * @since 2025/8/24 上午12:49
 */
public interface UserService {
    /**
     * 生成图形验证码
     */
    Result<CaptchaVO> getCaptcha();

    /**
     * 用户注册接口
     */
    Result<RegisterVO> register(RegisterDTO registerDTO);
}
