package com.han.cafe.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.han.cafe.common.ApiResponse;
import com.han.cafe.dto.AdminLoginDTO;
import com.han.cafe.entity.Admin;
import com.han.cafe.exception.BusinessException;
import com.han.cafe.service.AdminService;
import com.han.cafe.vo.AdminLoginVO;
import com.han.cafe.vo.AdminVO;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Resource
    private AdminService adminService;

    /**
     * 管理员登录
     */
    @PostMapping("/login")
    public ApiResponse<AdminLoginVO> login(@Valid @RequestBody AdminLoginDTO loginDTO) {
        try {
            AdminLoginVO loginVO = adminService.login(loginDTO.getUsername(), loginDTO.getPassword());
            return ApiResponse.success(loginVO);
        } catch (Exception e) {
            log.error("管理员登录失败: {}", e.getMessage(), e);
            throw new BusinessException("登录失败: " + e.getMessage());
        }
    }

    /**
     * 获取管理员列表
     */
    @GetMapping("/list")
    public ApiResponse<List<AdminVO>> getAdminList() {
        try {
            List<AdminVO> admins = adminService.getAdminList();
            return ApiResponse.success(admins);
        } catch (Exception e) {
            log.error("获取管理员列表失败: {}", e.getMessage(), e);
            throw new BusinessException("获取管理员列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取管理员详情
     */
    @GetMapping("/{adminId}")
    public ApiResponse<AdminVO> getAdminDetail(@PathVariable Integer adminId) {
        try {
            AdminVO admin = adminService.getAdminDetail(adminId);
            return ApiResponse.success(admin);
        } catch (Exception e) {
            log.error("获取管理员详情失败: {}", e.getMessage(), e);
            throw new BusinessException("获取管理员详情失败: " + e.getMessage());
        }
    }

    /**
     * 添加管理员
     */
    @PostMapping("/add")
    public ApiResponse<Boolean> addAdmin(@Valid @RequestBody Admin admin) {
        try {
            boolean result = adminService.addAdmin(admin);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("添加管理员失败: {}", e.getMessage(), e);
            throw new BusinessException("添加管理员失败: " + e.getMessage());
        }
    }

    /**
     * 更新管理员信息
     */
    @PutMapping("/update")
    public ApiResponse<Boolean> updateAdmin(@Valid @RequestBody Admin admin) {
        try {
            boolean result = adminService.updateAdmin(admin);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("更新管理员信息失败: {}", e.getMessage(), e);
            throw new BusinessException("更新管理员信息失败: " + e.getMessage());
        }
    }

    /**
     * 删除管理员
     */
    @DeleteMapping("/{adminId}")
    public ApiResponse<Boolean> deleteAdmin(@PathVariable Integer adminId) {
        try {
            boolean result = adminService.deleteAdmin(adminId);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("删除管理员失败: {}", e.getMessage(), e);
            throw new BusinessException("删除管理员失败: " + e.getMessage());
        }
    }

    /**
     * 重置管理员密码
     */
    @PostMapping("/{adminId}/reset-password")
    public ApiResponse<Boolean> resetPassword(@PathVariable Integer adminId, @RequestBody String newPassword) {
        try {
            boolean result = adminService.resetPassword(adminId, newPassword);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("重置管理员密码失败: {}", e.getMessage(), e);
            throw new BusinessException("重置管理员密码失败: " + e.getMessage());
        }
    }
} 