package com.han.cafe.service;

import com.han.cafe.entity.User;
import com.han.cafe.vo.LoginVO;
import com.han.cafe.vo.RegisterVO;

public interface UserService {
    /**
     * 根据手机号查询用户
     * @param mobile 手机号
     * @return 用户对象
     */
    User getUserByMobile(String mobile);

    /**
     * 用户注册
     * @param registerVO 注册信息
     * @return 注册结果
     */
    User register(RegisterVO registerVO);

    /**
     * 用户登录
     * @param loginVO 登录信息
     * @return 用户信息
     */
    User login(LoginVO loginVO);
} 