package com.han.cafe.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.han.cafe.entity.Admin;
import com.han.cafe.exception.BusinessException;
import com.han.cafe.mapper.AdminMapper;
import com.han.cafe.service.AdminService;
import com.han.cafe.utils.JwtTokenUtil;
import com.han.cafe.vo.AdminLoginVO;
import com.han.cafe.vo.AdminVO;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AdminServiceImpl implements AdminService {

    @Resource
    private AdminMapper adminMapper;

    @Lazy
    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public AdminLoginVO login(String username, String password) {
        Admin admin = adminMapper.selectByUsername(username);
        if (admin == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 验证密码
        if (!passwordEncoder.matches(password, admin.getPassword())) {
            throw new BadCredentialsException("用户名或密码错误");
        }

        // 检查账号状态
        if (admin.getStatus() != 1) {
            throw new BusinessException("账号已被禁用");
        }

        // 更新最后登录时间
        admin.setLastLogin(LocalDateTime.now());
        adminMapper.updateById(admin);

        // 生成token
        String token = jwtTokenUtil.generateAdminToken(admin.getUsername());

        // 组装返回数据
        AdminLoginVO loginVO = new AdminLoginVO();
        loginVO.setAdminId(admin.getAdminId());
        loginVO.setUsername(admin.getUsername());
        loginVO.setName(admin.getName());
        loginVO.setAvatarUrl(admin.getAvatarUrl());
        loginVO.setRole(admin.getRole());
        loginVO.setToken(token);

        System.out.println(loginVO);

        return loginVO;
    }

    @Override
    public Admin findByUsername(String username) {
        return adminMapper.selectByUsername(username);
    }

    @Override
    public List<AdminVO> getAdminList() {
        List<Admin> admins = adminMapper.selectList(new LambdaQueryWrapper<>());
        return admins.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public AdminVO getAdminDetail(Integer adminId) {
        Admin admin = adminMapper.selectById(adminId);
        if (admin == null) {
            throw new BusinessException("管理员不存在");
        }
        return convertToVO(admin);
    }

    @Override
    @Transactional
    public boolean addAdmin(Admin admin) {
        // 检查用户名是否已存在
        Admin existingAdmin = adminMapper.selectByUsername(admin.getUsername());
        if (existingAdmin != null) {
            throw new BusinessException("用户名已存在");
        }

        // 设置默认值
        admin.setStatus(1);
        admin.setCreatedAt(LocalDateTime.now());
        admin.setUpdatedAt(LocalDateTime.now());

        // 密码加密
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));

        return adminMapper.insert(admin) > 0;
    }

    @Override
    @Transactional
    public boolean updateAdmin(Admin admin) {
        Admin existingAdmin = adminMapper.selectById(admin.getAdminId());
        if (existingAdmin == null) {
            throw new BusinessException("管理员不存在");
        }

        // 如果修改了用户名，检查是否有冲突
        if (!existingAdmin.getUsername().equals(admin.getUsername())) {
            Admin adminByUsername = adminMapper.selectByUsername(admin.getUsername());
            if (adminByUsername != null) {
                throw new BusinessException("用户名已存在");
            }
        }

        // 不更新密码
        admin.setPassword(null);
        admin.setUpdatedAt(LocalDateTime.now());

        return adminMapper.updateById(admin) > 0;
    }

    @Override
    @Transactional
    public boolean deleteAdmin(Integer adminId) {
        Admin admin = adminMapper.selectById(adminId);
        if (admin == null) {
            throw new BusinessException("管理员不存在");
        }

        // 不能删除超级管理员
        if (admin.getRole() == 1) {
            throw new BusinessException("不能删除超级管理员");
        }

        return adminMapper.deleteById(adminId) > 0;
    }

    @Override
    @Transactional
    public boolean resetPassword(Integer adminId, String newPassword) {
        Admin admin = adminMapper.selectById(adminId);
        if (admin == null) {
            throw new BusinessException("管理员不存在");
        }

        admin.setPassword(passwordEncoder.encode(newPassword));
        admin.setUpdatedAt(LocalDateTime.now());

        return adminMapper.updateById(admin) > 0;
    }

    /**
     * 转换为VO对象
     */
    private AdminVO convertToVO(Admin admin) {
        AdminVO vo = new AdminVO();
        BeanUtils.copyProperties(admin, vo);
        return vo;
    }
} 