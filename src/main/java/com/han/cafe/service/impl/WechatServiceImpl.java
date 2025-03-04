package com.han.cafe.service.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.han.cafe.entity.User;
import com.han.cafe.service.WechatService;
import com.han.cafe.vo.WechatLoginVO;

@Service
public class WechatServiceImpl implements WechatService {

    private static final Logger logger = LoggerFactory.getLogger(WechatServiceImpl.class);
    private static final String WECHAT_AUTH_URL = "https://api.weixin.qq.com/sns/jscode2session";
    private static final String WECHAT_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token";
    private static final String WECHAT_USER_INFO_URL = "https://api.weixin.qq.com/cgi-bin/user/info";

    @Value("${wechat.appid}")
    private String appid;

    @Value("${wechat.secret}")
    private String secret;

    private final UserQueryServiceImpl userQueryService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public WechatServiceImpl(UserQueryServiceImpl userQueryService) {
        this.userQueryService = userQueryService;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    @Transactional
    public User loginByWechat(WechatLoginVO loginVO) {
        try {
            logger.info("收到微信登录请求，用户信息：nickname={}, avatarUrl={}", 
                loginVO.getNickname(), loginVO.getAvatarUrl());

            // 调用微信接口获取openid和unionid
            String url = String.format("%s?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                    WECHAT_AUTH_URL, appid, secret, loginVO.getCode());
            
            String response = restTemplate.getForObject(url, String.class);
            JsonNode jsonNode = objectMapper.readTree(response);

            if (jsonNode.has("errcode") && jsonNode.get("errcode").asInt() != 0) {
                throw new RuntimeException("微信授权失败：" + jsonNode.get("errmsg").asText());
            }

            String openid = jsonNode.get("openid").asText();
            String unionid = jsonNode.has("unionid") ? jsonNode.get("unionid").asText() : null;

            // 查找或创建用户
            User user = findOrCreateWechatUser(openid, unionid, loginVO);
            
            // 更新最后登录时间
            user.setLastLogin(new Date());
            userQueryService.updateById(user);

            logger.info("微信登录成功，用户信息：userId={}, nickname={}, avatarUrl={}", 
                user.getUserId(), user.getNickname(), user.getAvatarUrl());

            return user;

        } catch (Exception e) {
            logger.error("微信登录失败", e);
            throw new RuntimeException("微信登录失败：" + e.getMessage());
        }
    }

    private String getAccessToken() throws Exception {
        String url = String.format("%s?grant_type=client_credential&appid=%s&secret=%s",
                WECHAT_ACCESS_TOKEN_URL, appid, secret);
        
        String response = restTemplate.getForObject(url, String.class);
        JsonNode jsonNode = objectMapper.readTree(response);

        if (jsonNode.has("errcode") && jsonNode.get("errcode").asInt() != 0) {
            throw new RuntimeException("获取access_token失败：" + jsonNode.get("errmsg").asText());
        }

        return jsonNode.get("access_token").asText();
    }

    private JsonNode getUserInfo(String openid, String accessToken) throws Exception {
        String url = String.format("%s?access_token=%s&openid=%s&lang=zh_CN",
                WECHAT_USER_INFO_URL, accessToken, openid);
        
        String response = restTemplate.getForObject(url, String.class);
        JsonNode jsonNode = objectMapper.readTree(response);

        if (jsonNode.has("errcode") && jsonNode.get("errcode").asInt() != 0) {
            throw new RuntimeException("获取用户信息失败：" + jsonNode.get("errmsg").asText());
        }

        return jsonNode;
    }

    private User findOrCreateWechatUser(String openid, String unionid, WechatLoginVO loginVO) {
        // 根据openid查找用户
        User user = userQueryService.findByWechatOpenid(openid);
        
        if (user == null) {
            // 创建新用户
            user = new User();
            user.setWechatOpenid(openid);
            user.setWechatUnionid(unionid);
            user.setStatus(1);
            user.setCreatedAt(new Date());
            user.setUpdatedAt(new Date());
            
            // 设置用户信息
            if (loginVO.getNickname() != null && !loginVO.getNickname().isEmpty()) {
                user.setNickname(loginVO.getNickname());
            } else {
                user.setNickname("微信用户");
            }
            
            if (loginVO.getAvatarUrl() != null && !loginVO.getAvatarUrl().isEmpty()) {
                user.setAvatarUrl(loginVO.getAvatarUrl());
            }
            
            // 保存新用户
            userQueryService.save(user);
            logger.info("创建新的微信用户: openid={}, nickname={}", openid, user.getNickname());
        } else {
            // 更新现有用户信息
            boolean needUpdate = false;
            
            if (loginVO.getNickname() != null && !loginVO.getNickname().isEmpty() 
                && !loginVO.getNickname().equals(user.getNickname())) {
                user.setNickname(loginVO.getNickname());
                needUpdate = true;
            }
            
            if (loginVO.getAvatarUrl() != null && !loginVO.getAvatarUrl().isEmpty() 
                && !loginVO.getAvatarUrl().equals(user.getAvatarUrl())) {
                user.setAvatarUrl(loginVO.getAvatarUrl());
                needUpdate = true;
            }
            
            if (needUpdate) {
                user.setUpdatedAt(new Date());
                userQueryService.updateById(user);
                logger.info("更新现有微信用户信息: openid={}, nickname={}", openid, user.getNickname());
            } else {
                logger.info("无需更新微信用户信息: openid={}, nickname={}", openid, user.getNickname());
            }
        }
        
        return user;
    }
} 