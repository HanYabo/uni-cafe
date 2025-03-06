package com.han.cafe.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.han.cafe.entity.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("SELECT * FROM user WHERE mobile = #{mobile}")
    User selectByMobile(String mobile);
    
    @Select("SELECT * FROM user WHERE wechat_openid = #{openid}")
    User selectByWechatOpenid(String openid);
    
    @Select("SELECT * FROM user WHERE wechat_unionid = #{unionid}")
    User selectByWechatUnionid(String unionid);
}