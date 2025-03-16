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
    
    /**
     * 获取分类及其商品列表
     * @return 分类及其商品列表
     */
    List<CategoryProductVO> getCategoriesWithProducts();
    
    /**
     * 获取分类及其商品列表（带缓存）
     * @return 分类及其商品列表
     */
    List<CategoryProductVO> getCategoriesWithProductsFromCache();
    
    /**
     * 获取所有启用的分类（带缓存）
     * @return 所有启用的分类
     */
    List<Category> listEnabledCategoriesFromCache();
    
    /**
     * 根据ID获取分类（带缓存）
     * @param categoryId 分类ID
     * @return 分类信息
     */
    Category getCategoryByIdFromCache(Integer categoryId);
    
    /**
     * 清除分类相关缓存
     * @param categoryId 分类ID，如果为null则清除所有分类缓存
     */
    void clearCategoryCache(Integer categoryId);
}
