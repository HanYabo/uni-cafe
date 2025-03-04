package com.han.cafe.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.han.cafe.entity.User;
import com.han.cafe.mapper.UserMapper;

@Service
public class UserQueryServiceImpl {

    private final UserMapper userMapper;

    public UserQueryServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public User findByMobile(String mobile) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getMobile, mobile);
        return userMapper.selectOne(queryWrapper);
    }

    public User findByWechatOpenid(String openid) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getWechatOpenid, openid);
        return userMapper.selectOne(queryWrapper);
    }

    public boolean save(User user) {
        return userMapper.insert(user) > 0;
    }

    public boolean updateById(User user) {
        return userMapper.updateById(user) > 0;
    }
} 