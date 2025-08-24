package com.cutejiuge.api.service;

import com.cutejiuge.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Spring Security用户详情服务实现
 *
 * @author cutejiuge
 * @since 2025/8/24 上午12:47
 */
@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    // 用户服务RPC接口
    @DubboReference
    private UserService userService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}
