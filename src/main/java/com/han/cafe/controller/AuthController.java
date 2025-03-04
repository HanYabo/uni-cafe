package com.han.cafe.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.han.cafe.common.R;
import com.han.cafe.entity.User;
import com.han.cafe.service.UserService;
import com.han.cafe.service.WechatService;
import com.han.cafe.utils.JwtTokenUtil;
import com.han.cafe.vo.LoginVO;
import com.han.cafe.vo.RegisterVO;
import com.han.cafe.vo.WechatLoginVO;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private WechatService wechatService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    // 注册
    @PostMapping(value = "/register", 
                consumes = {MediaType.APPLICATION_JSON_VALUE})
    public R<?> register(@Valid @RequestBody RegisterVO registerVO) {
        try {
            // 注册用户
            User user = userService.register(registerVO);
            
            // 生成JWT令牌
            String token = jwtTokenUtil.generateToken(
                org.springframework.security.core.userdetails.User.builder()
                    .username(user.getMobile())
                    .password("")
                    .authorities("ROLE_USER")
                    .build()
            );

            // 返回完整的用户信息
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("tokenType", "Bearer");
            data.put("userInfo", user);
            return R.ok(data);
        } catch (Exception e) {
            return R.badRequest(e.getMessage());
        }
    }

    // 登录
    @PostMapping(value = "/login", 
                consumes = {MediaType.APPLICATION_JSON_VALUE})
    public R<?> login(@Valid @RequestBody LoginVO loginVO) {
        try {
            // 登录并获取用户信息
            User user = userService.login(loginVO);
            
            // 生成JWT令牌
            String token = jwtTokenUtil.generateToken(
                org.springframework.security.core.userdetails.User.builder()
                    .username(user.getMobile())
                    .password("")
                    .authorities("ROLE_USER")
                    .build()
            );

            // 返回完整的用户信息
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("tokenType", "Bearer");
            data.put("userInfo", user);
            return R.ok(data);
        } catch (BadCredentialsException e) {
            return R.unauthorized("用户名或密码错误");
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    // 微信一键登录
    @PostMapping(value = "/wechat/login",
                consumes = {MediaType.APPLICATION_JSON_VALUE})
    public R<?> wechatLogin(@Valid @RequestBody WechatLoginVO wechatLoginVO) {
        try {
            // 微信登录
            User user = wechatService.loginByWechat(wechatLoginVO);
            
            // 生成JWT令牌
            String token = jwtTokenUtil.generateToken(
                org.springframework.security.core.userdetails.User.builder()
                    .username(user.getWechatOpenid())
                    .password("")
                    .authorities("ROLE_USER")
                    .build()
            );

            // 返回完整的用户信息
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("tokenType", "Bearer");
            data.put("userInfo", user);
            System.out.println("data: " + data);
            return R.ok(data);
        } catch (Exception e) {
            return R.error("微信登录失败：" + e.getMessage());
        }
    }
} 