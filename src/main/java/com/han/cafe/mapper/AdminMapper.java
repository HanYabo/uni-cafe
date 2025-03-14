package com.han.cafe.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.han.cafe.entity.Admin;

@Mapper
public interface AdminMapper extends BaseMapper<Admin> {
    
    /**
     * 根据用户名查找管理员
     */
    @Select("SELECT * FROM admin WHERE username = #{username}")
    Admin selectByUsername(@Param("username") String username);
} 