package com.han.cafe.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.han.cafe.entity.Category;
import com.han.cafe.vo.CategoryProductVO;

/**
* @author MuY1eee
* @description 针对表【category(商品分类表)】的数据库操作Service
* @createDate 2025-03-04 20:14:37
*/
public interface CategoryService extends IService<Category> {
    List<CategoryProductVO> getCategoriesWithProducts();
}
