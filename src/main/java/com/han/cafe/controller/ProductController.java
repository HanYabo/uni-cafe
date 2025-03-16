package com.han.cafe.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.han.cafe.dto.ProductWithSpecsDTO;
import com.han.cafe.entity.Product;
import com.han.cafe.service.ProductService;
import com.han.cafe.utils.Result;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * 商品相关接口
 */
@Slf4j
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Resource
    private ProductService productService;

    /**
     * 获取所有商品列表
     */
    @GetMapping
    public Result<List<Product>> list() {
        List<Product> productList = productService.listProductsWithCache();
        return Result.success(productList);
    }

    /**
     * 根据ID获取商品详情
     */
    @GetMapping("/{id}")
    public Result<Product> getById(@PathVariable Integer id) {
        Product product = productService.getProductByIdWithCache(id);
        if (product == null) {
            return Result.fail("商品不存在");
        }
        return Result.success(product);
    }

    /**
     * 根据分类ID获取商品列表
     */
    @GetMapping("/category/{categoryId}")
    public Result<List<Product>> listByCategoryId(@PathVariable Integer categoryId) {
        List<Product> productList = productService.listProductsByCategoryIdWithCache(categoryId);
        return Result.success(productList);
    }
    
    /**
     * 获取热销商品排行榜
     * @param limit 返回的商品数量，默认为5
     * @return 热销商品列表（按销量降序排序）
     */
    @GetMapping("/hot")
    public Result<List<ProductWithSpecsDTO>> getHotProducts(@RequestParam(defaultValue = "5") int limit) {
        log.info("获取热销商品排行榜，数量限制：{}", limit);
        
        // 限制最大返回数量为50，避免过大的查询压力
        if (limit > 50) {
            limit = 50;
            log.warn("请求的热销商品数量超过限制，已调整为最大值50");
        }
        
        // 获取带规格信息的热销商品
        List<ProductWithSpecsDTO> hotProducts = productService.getHotProductsWithSpecsFromCache(limit);
        
        log.info("成功返回热销商品排行榜，共 {} 个商品", hotProducts.size());
        return Result.success(hotProducts);
    }
    
    /**
     * 同步商品销量数据到Redis排行
     * @return 同步结果
     */
    @GetMapping("/hot/sync")
    public Result<String> syncHotProducts() {
        log.info("同步商品销量数据到Redis排行榜");
        
        try {
            productService.syncProductSalesToRedis();
            return Result.success("同步成功");
        } catch (Exception e) {
            log.error("同步商品销量数据失败", e);
            return Result.fail("同步失败：" + e.getMessage());
        }
    }
} 