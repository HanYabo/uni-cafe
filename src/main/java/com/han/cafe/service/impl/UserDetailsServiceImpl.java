package com.han.cafe.service.impl;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.han.cafe.entity.User;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserQueryServiceImpl userQueryService;

    public UserDetailsServiceImpl(UserQueryServiceImpl userQueryService) {
        this.userQueryService = userQueryService;
    }

    @Override
    public UserDetails loadUserByUsername(String mobile) throws UsernameNotFoundException {
        User user = userQueryService.findByMobile(mobile);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        // 用户存在，返回SpringSecurity的UserDetails对象
        return new org.springframework.security.core.userdetails.User(
                user.getMobile(),
                user.getPassword(),
                user.getStatus() == 1, // 状态为1表示启用
                true, // 账号未过期
                true, // 凭证未过期
                true, // 账号未锁定
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
} 