package com.han.cafe.vo;

import lombok.Data;

/**
 * 管理员登录返回数据
 */
@Data
public class AdminLoginVO {
    /**
     * 管理员ID
     */
    private Integer adminId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 姓名
     */
    private String name;
    
    /**
     * 头像URL
     */
    private String avatarUrl;
    
    /**
     * 角色 1-超级管理员 2-普通管理员
     */
    private Integer role;
    
    /**
     * 登录令牌
     */
    private String token;
} 