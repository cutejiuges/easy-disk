package com.cutejiuge.user.service;

import com.cutejiuge.common.response.Result;
import com.cutejiuge.user.pojo.dto.RegisterDTO;
import com.cutejiuge.user.pojo.vo.CaptchaVO;
import com.cutejiuge.user.pojo.vo.RegisterVO;

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
