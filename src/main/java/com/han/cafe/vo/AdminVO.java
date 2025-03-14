package com.han.cafe.vo;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 管理员信息展示
 */
@Data
public class AdminVO {
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
     * 状态 0-禁用 1-正常
     */
    private Integer status;
    
    /**
     * 角色 1-超级管理员 2-普通管理员
     */
    private Integer role;
    
    /**
     * 最后登录时间
     */
    private LocalDateTime lastLogin;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
} 