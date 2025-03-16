package com.han.cafe.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.han.cafe.dto.ProductWithSpecsDTO;
import com.han.cafe.entity.Product;

/**
* @author MuY1eee
* @description 针对表【product(商品主表)】的数据库操作Service
* @createDate 2025-03-04 20:14:41
*/
public interface ProductService extends IService<Product> {
    
    /**
     * 获取所有商品列表，优先从缓存获取
     * @return 商品列表
     */
    List<Product> listProductsWithCache();
    
    /**
     * 根据ID获取商品，优先从缓存获取
     * @param id 商品ID
     * @return 商品信息
     */
    Product getProductByIdWithCache(Integer id);
    
    /**
     * 根据分类ID获取商品列表，优先从缓存获取
     * @param categoryId 分类ID
     * @return 商品列表
     */
    List<Product> listProductsByCategoryIdWithCache(Integer categoryId);
    
    /**
     * 清除商品缓存
     * @param id 商品ID，为null时清除所有商品缓存
     */
    void clearProductCache(Integer id);
    
    /**
     * 从缓存中获取带规格信息的热销商品排行榜
     * @param limit 返回的商品数量
     * @return 带规格信息的热销商品列表，按销量降序排列
     */
    List<ProductWithSpecsDTO> getHotProductsWithSpecsFromCache(int limit);
    
    /**
     * 同步所有商品销量数据到Redis排行榜
     * 将数据库中的商品销量同步到Redis的Sorted Set中
     */
    void syncProductSalesToRedis();
    
    /**
     * 增加指定商品的销量并更新排行榜
     * @param productId 商品ID
     * @param increment 增加的销量
     */
    void incrementProductSales(Integer productId, int increment);
}
