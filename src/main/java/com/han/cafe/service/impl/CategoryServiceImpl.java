package com.han.cafe.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.han.cafe.entity.Category;
import com.han.cafe.entity.Product;
import com.han.cafe.entity.ProductSpec;
import com.han.cafe.entity.Spec;
import com.han.cafe.entity.SpecValue;
import com.han.cafe.mapper.CategoryMapper;
import com.han.cafe.mapper.ProductMapper;
import com.han.cafe.mapper.ProductSpecMapper;
import com.han.cafe.mapper.SpecMapper;
import com.han.cafe.mapper.SpecValueMapper;
import com.han.cafe.service.CategoryService;
import com.han.cafe.utils.RedisUtils;
import com.han.cafe.vo.CategoryProductVO;
import com.han.cafe.vo.ProductSpecVO;
import com.han.cafe.vo.ProductVO;
import com.han.cafe.vo.SpecValueVO;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
* @author MuY1eee
* @description 针对表【category(商品分类表)】的数据库操作Service实现
* @createDate 2025-03-04 20:14:37
*/
@Slf4j
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
    implements CategoryService{

    @Resource
    private ProductMapper productMapper;

    @Resource
    private ProductSpecMapper productSpecMapper;
    
    @Resource
    private SpecMapper specMapper;
    
    @Resource
    private SpecValueMapper specValueMapper;
    
    @Resource
    private RedisUtils redisUtils;
    
    // 缓存前缀，避免与其他业务的缓存key冲突
    private static final String CACHE_KEY_PREFIX = "category:";
    
    // 分类列表的缓存key
    private static final String CACHE_CATEGORY_LIST = CACHE_KEY_PREFIX + "list:enabled";
    
    // 分类详情的缓存key前缀
    private static final String CACHE_CATEGORY_DETAIL = CACHE_KEY_PREFIX + "detail:";
    
    // 分类及商品的缓存key
    private static final String CACHE_CATEGORY_PRODUCTS = CACHE_KEY_PREFIX + "with:products";
    
    // 缓存过期时间（24小时 = 86400秒）
    private static final long CACHE_EXPIRE_TIME = 86400;

    @Override
    public List<CategoryProductVO> getCategoriesWithProducts() {
        // 1. 查询所有启用的分类
        LambdaQueryWrapper<Category> categoryWrapper = new LambdaQueryWrapper<>();
        categoryWrapper.eq(Category::getStatus, 1)
                .orderByAsc(Category::getSortOrder);
        List<Category> categories = this.list(categoryWrapper);

        // 2. 查询所有启用的商品
        LambdaQueryWrapper<Product> productWrapper = new LambdaQueryWrapper<>();
        productWrapper.eq(Product::getStatus, 1)
                .orderByAsc(Product::getSortOrder);
        List<Product> products = productMapper.selectList(productWrapper);

        // 3. 获取所有商品ID
        List<Integer> productIds = products.stream()
                .map(Product::getProductId)
                .collect(Collectors.toList());

        // 4. 查询这些商品关联的规格
        LambdaQueryWrapper<ProductSpec> productSpecWrapper = new LambdaQueryWrapper<>();
        productSpecWrapper.in(ProductSpec::getProductId, productIds)
                .orderByAsc(ProductSpec::getSortOrder);
        List<ProductSpec> productSpecs = productSpecMapper.selectList(productSpecWrapper);

        // 5. 获取所有规格ID
        List<Integer> specIds = productSpecs.stream()
                .map(ProductSpec::getSpecId)
                .distinct()
                .collect(Collectors.toList());

        // 6. 查询规格信息
        LambdaQueryWrapper<Spec> specWrapper = new LambdaQueryWrapper<>();
        specWrapper.in(Spec::getSpecId, specIds)
                .eq(Spec::getStatus, 1)
                .orderByAsc(Spec::getSortOrder);
        List<Spec> specs = specMapper.selectList(specWrapper);

        // 7. 查询规格值
        LambdaQueryWrapper<SpecValue> specValueWrapper = new LambdaQueryWrapper<>();
        specValueWrapper.in(SpecValue::getSpecId, specIds)
                .eq(SpecValue::getStatus, 1)
                .orderByAsc(SpecValue::getSortOrder);
        List<SpecValue> specValues = specValueMapper.selectList(specValueWrapper);

        // 8. 按规格ID分组规格值
        Map<Integer, List<SpecValue>> specValueMap = specValues.stream()
                .collect(Collectors.groupingBy(SpecValue::getSpecId));

        // 9. 按商品ID分组规格
        Map<Integer, List<ProductSpec>> productSpecMap = productSpecs.stream()
                .collect(Collectors.groupingBy(ProductSpec::getProductId));

        // 10. 组装商品VO
        List<ProductVO> productVOs = new ArrayList<>();
        for (Product product : products) {
            ProductVO productVO = new ProductVO();
            BeanUtils.copyProperties(product, productVO);
            
            // 获取商品的规格列表
            List<ProductSpec> currentProductSpecs = productSpecMap.getOrDefault(product.getProductId(), new ArrayList<>());
            List<ProductSpecVO> productSpecVOs = new ArrayList<>();
            
            for (ProductSpec productSpec : currentProductSpecs) {
                // 查找规格信息
                Spec spec = specs.stream()
                        .filter(s -> s.getSpecId().equals(productSpec.getSpecId()))
                        .findFirst()
                        .orElse(null);
                
                if (spec != null) {
                    ProductSpecVO productSpecVO = new ProductSpecVO();
                    productSpecVO.setSpecId(spec.getSpecId());
                    productSpecVO.setName(spec.getName());
                    productSpecVO.setSortOrder(productSpec.getSortOrder());
                    productSpecVO.setRequired(productSpec.getRequired());
                    
                    // 获取规格值列表
                    List<SpecValue> currentSpecValues = specValueMap.getOrDefault(spec.getSpecId(), new ArrayList<>());
                    List<SpecValueVO> specValueVOs = currentSpecValues.stream().map(value -> {
                        SpecValueVO vo = new SpecValueVO();
                        BeanUtils.copyProperties(value, vo);
                        return vo;
                    }).collect(Collectors.toList());
                    
                    productSpecVO.setValues(specValueVOs);
                    productSpecVOs.add(productSpecVO);
                }
            }
            
            productVO.setSpecs(productSpecVOs);
            productVOs.add(productVO);
        }

        // 11. 按分类ID分组商品
        Map<Integer, List<ProductVO>> productMap = productVOs.stream()
                .collect(Collectors.groupingBy(ProductVO::getCategoryId));

        // 12. 组装最终返回数据
        List<CategoryProductVO> result = new ArrayList<>();
        for (Category category : categories) {
            CategoryProductVO vo = new CategoryProductVO();
            BeanUtils.copyProperties(category, vo);
            vo.setProducts(productMap.getOrDefault(category.getCategoryId(), new ArrayList<>()));
            result.add(vo);
        }

        return result;
    }
    
    @Override
    public List<CategoryProductVO> getCategoriesWithProductsFromCache() {
        log.info("从缓存获取分类及商品数据");
        // 尝试从缓存获取
        Object cacheValue = redisUtils.get(CACHE_CATEGORY_PRODUCTS);
        if (cacheValue != null) {
            log.info("缓存命中：{}", CACHE_CATEGORY_PRODUCTS);
            return (List<CategoryProductVO>) cacheValue;
        }
        
        // 缓存未命中，从数据库查询
        log.info("缓存未命中，从数据库查询分类及商品数据");
        List<CategoryProductVO> result = getCategoriesWithProducts();
        
        // 将查询结果存入缓存
        redisUtils.set(CACHE_CATEGORY_PRODUCTS, result, CACHE_EXPIRE_TIME);
        log.info("分类及商品数据已存入缓存，过期时间：{}秒", CACHE_EXPIRE_TIME);
        
        return result;
    }

    @Override
    public List<Category> listEnabledCategoriesFromCache() {
        log.info("从缓存获取启用的分类列表");
        // 尝试从缓存获取
        Object cacheValue = redisUtils.get(CACHE_CATEGORY_LIST);
        if (cacheValue != null) {
            log.info("缓存命中：{}", CACHE_CATEGORY_LIST);
            return (List<Category>) cacheValue;
        }
        
        // 缓存未命中，从数据库查询
        log.info("缓存未命中，从数据库查询启用的分类列表");
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getStatus, 1)
                .orderByAsc(Category::getSortOrder);
        List<Category> categories = this.list(queryWrapper);
        
        // 将查询结果存入缓存
        redisUtils.set(CACHE_CATEGORY_LIST, categories, CACHE_EXPIRE_TIME);
        log.info("启用的分类列表已存入缓存，过期时间：{}秒", CACHE_EXPIRE_TIME);
        
        return categories;
    }

    @Override
    public Category getCategoryByIdFromCache(Integer categoryId) {
        if (categoryId == null) {
            return null;
        }
        
        // 构造缓存key
        String cacheKey = CACHE_CATEGORY_DETAIL + categoryId;
        log.info("从缓存获取分类详情，cacheKey：{}", cacheKey);
        
        // 尝试从缓存获取
        Object cacheValue = redisUtils.get(cacheKey);
        if (cacheValue != null) {
            log.info("缓存命中：{}", cacheKey);
            return (Category) cacheValue;
        }
        
        // 缓存未命中，从数据库查询
        log.info("缓存未命中，从数据库查询分类详情");
        Category category = this.getById(categoryId);
        
        // 如果数据库中存在该分类，则将其存入缓存
        if (category != null) {
            redisUtils.set(cacheKey, category, CACHE_EXPIRE_TIME);
            log.info("分类详情已存入缓存，过期时间：{}秒", CACHE_EXPIRE_TIME);
        } else {
            // 对于不存在的数据，也缓存null值，但过期时间较短，避免缓存穿透
            redisUtils.set(cacheKey, null, 60); // 60秒
            log.info("空结果已存入缓存，过期时间：60秒（防止缓存穿透）");
        }
        
        return category;
    }

    @Override
    public void clearCategoryCache(Integer categoryId) {
        if (categoryId == null) {
            // 清除所有分类相关缓存
            log.info("清除所有分类相关缓存");
            redisUtils.delete(CACHE_CATEGORY_LIST);
            redisUtils.delete(CACHE_CATEGORY_PRODUCTS);
            
            // TODO: 如果分类较多，这种方式不太合适，可能需要使用Redis的keys命令模糊匹配删除
            // 此处简化处理，实际项目中可以考虑使用Redis的Scan命令
        } else {
            // 清除指定分类的缓存
            log.info("清除分类ID为{}的缓存", categoryId);
            String detailCacheKey = CACHE_CATEGORY_DETAIL + categoryId;
            redisUtils.delete(detailCacheKey);
            
            // 同时清除分类列表和分类商品列表的缓存
            redisUtils.delete(CACHE_CATEGORY_LIST);
            redisUtils.delete(CACHE_CATEGORY_PRODUCTS);
        }
    }
    
    // 重写基础增删改方法，添加缓存处理逻辑
    
    @Override
    public boolean save(Category entity) {
        boolean result = super.save(entity);
        if (result) {
            clearCategoryCache(entity.getCategoryId());
        }
        return result;
    }
    
    @Override
    public boolean updateById(Category entity) {
        boolean result = super.updateById(entity);
        if (result) {
            clearCategoryCache(entity.getCategoryId());
        }
        return result;
    }
    
    @Override
    public boolean removeById(Category entity) {
        boolean result = super.removeById(entity);
        if (result) {
            clearCategoryCache(entity.getCategoryId());
        }
        return result;
    }
}




