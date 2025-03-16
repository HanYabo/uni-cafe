package com.han.cafe.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.han.cafe.entity.Category;
import com.han.cafe.service.CategoryService;
import com.han.cafe.utils.Result;
import com.han.cafe.vo.CategoryProductVO;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    /**
     * 获取分类及商品信息（带缓存）
     */
    @GetMapping("/with-products")
    public Result<List<CategoryProductVO>> getCategoriesWithProducts() {
        try {
            log.info("开始获取分类及商品信息（使用缓存）");
            List<CategoryProductVO> result = categoryService.getCategoriesWithProductsFromCache();
            log.info("成功获取分类及商品信息，共 {} 个分类", result.size());
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取分类及商品信息失败：", e);
            return Result.fail("获取分类及商品信息失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取所有启用的分类（带缓存）
     */
    @GetMapping
    public Result<List<Category>> getEnabledCategories() {
        try {
            log.info("开始获取所有启用的分类（使用缓存）");
            List<Category> categories = categoryService.listEnabledCategoriesFromCache();
            log.info("成功获取所有启用的分类，共 {} 个分类", categories.size());
            return Result.success(categories);
        } catch (Exception e) {
            log.error("获取所有启用的分类失败：", e);
            return Result.fail("获取所有启用的分类失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取分类（带缓存）
     */
    @GetMapping("/{categoryId}")
    public Result<Category> getCategoryById(@PathVariable Integer categoryId) {
        try {
            log.info("开始获取分类详情，categoryId: {}（使用缓存）", categoryId);
            Category category = categoryService.getCategoryByIdFromCache(categoryId);
            if (category == null) {
                return Result.fail("分类不存在");
            }
            return Result.success(category);
        } catch (Exception e) {
            log.error("获取分类详情失败：", e);
            return Result.fail("获取分类详情失败：" + e.getMessage());
        }
    }
    
    /**
     * 清除分类缓存（用于管理员操作后刷新缓存）
     * 实际项目中应该添加权限控制
     */
    @GetMapping("/clear-cache")
    public Result<Void> clearCategoryCache() {
        try {
            log.info("清除所有分类缓存");
            categoryService.clearCategoryCache(null);
            return Result.success();
        } catch (Exception e) {
            log.error("清除分类缓存失败：", e);
            return Result.fail("清除分类缓存失败：" + e.getMessage());
        }
    }
} 