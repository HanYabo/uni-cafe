package com.han.cafe.service;

import com.han.cafe.entity.User;
import com.han.cafe.vo.WechatLoginVO;

public interface WechatService {
    /**
     * 微信登录
     * @param loginVO 登录信息，包含code和用户资料
     * @return 用户信息
     */
    User loginByWechat(WechatLoginVO loginVO);
} 