package com.han.cafe.service;

import java.util.List;

import com.han.cafe.entity.Admin;
import com.han.cafe.vo.AdminLoginVO;
import com.han.cafe.vo.AdminVO;

public interface AdminService {
    
    /**
     * 管理员登录
     * @param username 用户名
     * @param password 密码
     * @return 登录成功返回token等信息
     */
    AdminLoginVO login(String username, String password);
    
    /**
     * 根据用户名查找管理员
     * @param username 用户名
     * @return 管理员信息
     */
    Admin findByUsername(String username);
    
    /**
     * 获取管理员列表
     * @return 管理员列表
     */
    List<AdminVO> getAdminList();
    
    /**
     * 获取管理员详情
     * @param adminId 管理员ID
     * @return 管理员详情
     */
    AdminVO getAdminDetail(Integer adminId);
    
    /**
     * 添加管理员
     * @param admin 管理员信息
     * @return 是否成功
     */
    boolean addAdmin(Admin admin);
    
    /**
     * 更新管理员信息
     * @param admin 管理员信息
     * @return 是否成功
     */
    boolean updateAdmin(Admin admin);
    
    /**
     * 删除管理员
     * @param adminId 管理员ID
     * @return 是否成功
     */
    boolean deleteAdmin(Integer adminId);
    
    /**
     * 重置管理员密码
     * @param adminId 管理员ID
     * @param newPassword 新密码
     * @return 是否成功
     */
    boolean resetPassword(Integer adminId, String newPassword);
} 