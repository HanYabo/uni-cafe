package com.han.cafe.service.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.han.cafe.entity.User;
import com.han.cafe.mapper.UserMapper;
import com.han.cafe.service.UserService;
import com.han.cafe.utils.JwtTokenUtil;
import com.han.cafe.vo.LoginVO;
import com.han.cafe.vo.RegisterVO;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserQueryServiceImpl userQueryService;

    public UserServiceImpl(PasswordEncoder passwordEncoder, 
                          AuthenticationManager authenticationManager,
                          JwtTokenUtil jwtTokenUtil,
                          UserQueryServiceImpl userQueryService) {
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userQueryService = userQueryService;
    }

    @Override
    public User getUserByMobile(String mobile) {
        return userQueryService.findByMobile(mobile);
    }

    @Override
    @Transactional
    public User register(RegisterVO registerVO) {
        // 检查手机号是否已注册
        User existingUser = getUserByMobile(registerVO.getMobile());
        if (existingUser != null) {
            throw new RuntimeException("该手机号已注册");
        }

        // 创建新用户
        User user = new User();
        user.setMobile(registerVO.getMobile());
        // 密码加密
        user.setPassword(passwordEncoder.encode(registerVO.getPassword()));
        user.setNickname(registerVO.getNickname());
        user.setAvatarUrl(registerVO.getAvatarUrl());
        user.setStatus(1); // 1-正常
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());

        // 保存用户
        userQueryService.save(user);
        return user;
    }

    @Override
    @Transactional
    public User login(LoginVO loginVO) {
        try {
            logger.debug("开始处理登录请求: {}", loginVO.getMobile());
            
            // 创建未认证的Authentication
            UsernamePasswordAuthenticationToken authenticationToken = 
                    new UsernamePasswordAuthenticationToken(loginVO.getMobile(), loginVO.getPassword());
            
            // 认证
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            
            // 保存认证信息
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // 获取用户信息
            User user = getUserByMobile(loginVO.getMobile());
            if (user == null) {
                throw new RuntimeException("用户不存在");
            }
            
            // 更新最后登录时间
            user.setLastLogin(new Date());
            userQueryService.updateById(user);
            logger.debug("用户 {} 登录成功，更新最后登录时间", user.getMobile());
            
            return user;
            
        } catch (BadCredentialsException e) {
            logger.error("登录失败：用户名或密码错误 - {}", loginVO.getMobile(), e);
            throw new BadCredentialsException("用户名或密码错误");
        } catch (Exception e) {
            logger.error("登录过程发生异常 - {}", e.getMessage(), e);
            throw new RuntimeException("登录失败：" + e.getMessage());
        }
    }
} 