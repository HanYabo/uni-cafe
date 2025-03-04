package com.han.cafe.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.han.cafe.entity.Category;

/**
* @author MuY1eee
* @description 针对表【category(商品分类表)】的数据库操作Mapper
* @createDate 2025-03-04 20:14:37
* @Entity com.han.cafe.entity.Category
*/
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

}




