package com.han.cafe.service.impl;

import java.util.Collections;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.han.cafe.entity.Admin;
import com.han.cafe.service.AdminService;

import jakarta.annotation.Resource;

@Lazy
@Service("adminUserDetailsService")
public class AdminUserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private AdminService adminService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Admin admin = adminService.findByUsername(username);
        if (admin == null) {
            throw new UsernameNotFoundException("管理员不存在");
        }

        // 根据角色分配权限
        String role = admin.getRole() == 1 ? "ROLE_SUPER_ADMIN" : "ROLE_ADMIN";

        // 用户存在，返回SpringSecurity的UserDetails对象
        return new org.springframework.security.core.userdetails.User(
                admin.getUsername(),
                admin.getPassword(),
                admin.getStatus() == 1, // 状态为1表示启用
                true, // 账号未过期
                true, // 凭证未过期
                true, // 账号未锁定
                Collections.singleton(new SimpleGrantedAuthority(role))
        );
    }
} 