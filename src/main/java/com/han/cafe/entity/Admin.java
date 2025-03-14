package com.han.cafe.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 管理员信息表
 */
@TableName(value ="admin")
@Data
public class Admin {
    /**
     * 管理员ID
     */
    @TableId(type = IdType.AUTO)
    private Integer adminId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 加密密码
     */
    private String password;

    /**
     * 姓名
     */
    private String name;

    /**
     * 头像URL
     */
    private String avatarUrl;

    /**
     * 0-禁用 1-正常
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

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 