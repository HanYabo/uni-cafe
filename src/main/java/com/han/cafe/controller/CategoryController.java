package com.han.cafe.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.han.cafe.common.ApiResponse;
import com.han.cafe.service.CategoryService;
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

    @GetMapping("/with-products")
    public ApiResponse<List<CategoryProductVO>> getCategoriesWithProducts() {
        try {
            log.info("开始获取分类及商品信息");
            List<CategoryProductVO> result = categoryService.getCategoriesWithProducts();
            log.info("成功获取分类及商品信息，共 {} 个分类", result.size());
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("获取分类及商品信息失败：", e);
            return ApiResponse.error("获取分类及商品信息失败：" + e.getMessage());
        }
    }
} 