package com.han.cafe.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.han.cafe.dto.ProductWithSpecsDTO;
import com.han.cafe.entity.Product;
import com.han.cafe.entity.ProductSpec;
import com.han.cafe.entity.Spec;
import com.han.cafe.entity.SpecValue;
import com.han.cafe.mapper.ProductMapper;
import com.han.cafe.mapper.ProductSpecMapper;
import com.han.cafe.mapper.SpecMapper;
import com.han.cafe.mapper.SpecValueMapper;
import com.han.cafe.service.ProductService;
import com.han.cafe.utils.RedisUtils;
import com.han.cafe.vo.ProductSpecVO;
import com.han.cafe.vo.SpecValueVO;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
* @author MuY1eee
* @description 针对表【product(商品主表)】的数据库操作Service实现
* @createDate 2025-03-04 20:14:41
*/
@Slf4j
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product>
    implements ProductService {
    
    @Resource
    private RedisUtils redisUtils;
    
    @Resource
    private ProductSpecMapper productSpecMapper;
    
    @Resource
    private SpecMapper specMapper;
    
    @Resource
    private SpecValueMapper specValueMapper;
    
    // 缓存前缀，避免与其他业务的缓存key冲突
    private static final String CACHE_KEY_PREFIX = "product:";
    // 所有商品列表的缓存key
    private static final String CACHE_PRODUCT_LIST = CACHE_KEY_PREFIX + "list";
    // 商品详情的缓存key前缀
    private static final String CACHE_PRODUCT_DETAIL = CACHE_KEY_PREFIX + "detail:";
    // 分类商品列表的缓存key前缀
    private static final String CACHE_PRODUCT_CATEGORY = CACHE_KEY_PREFIX + "category:";
    // 热销商品排行榜的缓存key
    private static final String CACHE_PRODUCT_HOT_RANK = CACHE_KEY_PREFIX + "hot:rank";
    // 缓存过期时间（3600秒 = 1小时）
    private static final long CACHE_EXPIRE_TIME = 3600;
    
    @Override
    public List<Product> listProductsWithCache() {
        // 尝试从缓存获取
        Object cacheValue = redisUtils.get(CACHE_PRODUCT_LIST);
        if (cacheValue != null) {
            return (List<Product>) cacheValue;
        }
        
        // 缓存未命中，从数据库查询
        List<Product> productList = list();
        
        // 将查询结果存入缓存
        redisUtils.set(CACHE_PRODUCT_LIST, productList, CACHE_EXPIRE_TIME);
        
        return productList;
    }
    
    @Override
    public Product getProductByIdWithCache(Integer id) {
        if (id == null) {
            return null;
        }
        
        // 构造缓存key
        String cacheKey = CACHE_PRODUCT_DETAIL + id;
        
        // 尝试从缓存获取
        Object cacheValue = redisUtils.get(cacheKey);
        if (cacheValue != null) {
            return (Product) cacheValue;
        }
        
        // 缓存未命中，从数据库查询
        Product product = getById(id);
        
        // 如果数据库中存在该商品，则将其存入缓存
        if (product != null) {
            redisUtils.set(cacheKey, product, CACHE_EXPIRE_TIME);
        }
        
        return product;
    }
    
    @Override
    public List<Product> listProductsByCategoryIdWithCache(Integer categoryId) {
        if (categoryId == null) {
            return listProductsWithCache();
        }
        
        // 构造缓存key
        String cacheKey = CACHE_PRODUCT_CATEGORY + categoryId;
        
        // 尝试从缓存获取
        Object cacheValue = redisUtils.get(cacheKey);
        if (cacheValue != null) {
            return (List<Product>) cacheValue;
        }
        
        // 缓存未命中，从数据库查询
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Product::getCategoryId, categoryId);
        List<Product> productList = list(queryWrapper);
        
        // 将查询结果存入缓存
        redisUtils.set(cacheKey, productList, CACHE_EXPIRE_TIME);
        
        return productList;
    }
    
    @Override
    public void clearProductCache(Integer id) {
        // 如果id为空，清除所有商品相关缓存
        if (id == null) {
            redisUtils.delete(CACHE_PRODUCT_LIST);
            // 清除所有分类下的商品列表缓存
            // 注意：如果分类较多，这种方式不太合适，可能需要使用Redis的keys命令模糊匹配删除
            // 这里简化处理，假设业务上已知分类不会太多
        } else {
            // 清除指定商品的缓存
            redisUtils.delete(CACHE_PRODUCT_DETAIL + id);
            // 同时清除商品列表缓存，因为单个商品的变化会影响列表
            redisUtils.delete(CACHE_PRODUCT_LIST);
            
            // 获取该商品所属的分类ID，并清除对应分类的商品列表缓存
            Product product = getById(id);
            if (product != null && product.getCategoryId() != null) {
                redisUtils.delete(CACHE_PRODUCT_CATEGORY + product.getCategoryId());
            }
        }
    }
    
    @Override
    public void syncProductSalesToRedis() {
        log.info("开始同步商品销量数据到Redis排行榜");
        
        // 从数据库获取所有上架的商品
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Product::getStatus, 1) // 状态为上架
                    .orderByDesc(Product::getMonthSales); // 按月销量降序
        List<Product> products = list(queryWrapper);
        
        // 清除旧的排行榜数据
        redisUtils.delete(CACHE_PRODUCT_HOT_RANK);
        
        // 将商品销量数据添加到Redis Sorted Set
        for (Product product : products) {
            if (product.getProductId() != null && product.getMonthSales() != null) {
                redisUtils.zAdd(CACHE_PRODUCT_HOT_RANK, product.getProductId().toString(), product.getMonthSales().doubleValue());
            }
        }
        
        log.info("商品销量数据同步到Redis排行榜完成，共 {} 个商品", products.size());
    }
    
    @Override
    public void incrementProductSales(Integer productId, int increment) {
        if (productId == null || increment <= 0) {
            return;
        }
        
        log.info("增加商品(ID:{})销量: +{}", productId, increment);
        
        // 1. 更新数据库中的销量
        Product product = getById(productId);
        if (product == null) {
            log.error("商品不存在，ID: {}", productId);
            return;
        }
        
        // 更新月销量
        Integer currentSales = product.getMonthSales();
        if (currentSales == null) {
            currentSales = 0;
        }
        product.setMonthSales(currentSales + increment);
        
        // 保存到数据库
        updateById(product);
        
        // 2. 同时更新Redis排行榜
        redisUtils.zIncrementScore(CACHE_PRODUCT_HOT_RANK, productId.toString(), increment);
        
        // 3. 更新商品缓存
        clearProductCache(productId);
        
        log.info("商品(ID:{})销量更新成功，新销量: {}", productId, product.getMonthSales());
    }
    
    // 重写基础增删改方法，添加缓存处理逻辑
    
    @Override
    public boolean save(Product entity) {
        boolean result = super.save(entity);
        if (result) {
            clearProductCache(entity.getProductId());
            // 如果是新商品，同时更新排行榜
            if (entity.getMonthSales() != null && entity.getMonthSales() > 0) {
                redisUtils.zAdd(CACHE_PRODUCT_HOT_RANK, entity.getProductId().toString(), entity.getMonthSales().doubleValue());
            }
        }
        return result;
    }
    
    @Override
    public boolean updateById(Product entity) {
        boolean result = super.updateById(entity);
        if (result) {
            clearProductCache(entity.getProductId());
            // 如果更新了销量，同时更新排行榜
            if (entity.getMonthSales() != null) {
                redisUtils.zAdd(CACHE_PRODUCT_HOT_RANK, entity.getProductId().toString(), entity.getMonthSales().doubleValue());
            }
        }
        return result;
    }
    
    @Override
    public boolean removeById(Product entity) {
        boolean result = super.removeById(entity);
        if (result) {
            clearProductCache(entity.getProductId());
            // 从排行榜中移除
            redisUtils.zRemove(CACHE_PRODUCT_HOT_RANK, entity.getProductId().toString());
        }
        return result;
    }
    
    @Override
    public List<ProductWithSpecsDTO> getHotProductsWithSpecsFromCache(int limit) {
        log.info("获取带规格信息的热销商品排行榜，TOP {}", limit);
        
        // 检查排行榜是否已存在，不存在则初始化
        if (!redisUtils.hasKey(CACHE_PRODUCT_HOT_RANK)) {
            log.info("热销商品排行榜缓存不存在，开始同步数据");
            syncProductSalesToRedis();
        }
        
        // 从Redis获取热销商品ID及销量（降序排列）
        Set<Object> productRankSet = redisUtils.zReverseRange(CACHE_PRODUCT_HOT_RANK, 0, limit - 1);
        
        if (productRankSet == null || productRankSet.isEmpty()) {
            log.info("排行榜为空，返回空列表");
            return new ArrayList<>();
        }
        
        // 从Redis获取商品ID及分数(销量)
        List<Integer> productIds = new ArrayList<>();
        for (Object obj : productRankSet) {
            if (obj instanceof String) {
                try {
                    productIds.add(Integer.parseInt((String) obj));
                } catch (NumberFormatException e) {
                    log.error("无法解析商品ID: {}", obj, e);
                }
            }
        }
        
        if (productIds.isEmpty()) {
            log.info("无法解析任何有效的商品ID，返回空列表");
            return new ArrayList<>();
        }
        
        // 批量获取商品详情
        List<Product> hotProducts = new ArrayList<>();
        for (Integer productId : productIds) {
            Product product = getProductByIdWithCache(productId);
            if (product != null) {
                hotProducts.add(product);
            }
        }
        
        // 按照排行榜顺序重新排序结果
        List<Product> orderedProducts = new ArrayList<>();
        for (Integer productId : productIds) {
            for (Product product : hotProducts) {
                if (product.getProductId().equals(productId)) {
                    orderedProducts.add(product);
                    break;
                }
            }
        }
        
        if (orderedProducts.isEmpty()) {
            log.info("未找到有效的热销商品，返回空列表");
            return new ArrayList<>();
        }
        
        // 获取所有商品ID
        List<Integer> orderedProductIds = orderedProducts.stream()
                .map(Product::getProductId)
                .collect(Collectors.toList());
        
        // 查询这些商品关联的规格
        LambdaQueryWrapper<ProductSpec> productSpecWrapper = new LambdaQueryWrapper<>();
        productSpecWrapper.in(ProductSpec::getProductId, orderedProductIds)
                .orderByAsc(ProductSpec::getSortOrder);
        List<ProductSpec> productSpecs = productSpecMapper.selectList(productSpecWrapper);
        
        // 如果没有规格信息，直接返回商品列表
        if (productSpecs.isEmpty()) {
            return orderedProducts.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }
        
        // 获取所有规格ID
        List<Integer> specIds = productSpecs.stream()
                .map(ProductSpec::getSpecId)
                .distinct()
                .collect(Collectors.toList());
        
        // 查询规格信息
        LambdaQueryWrapper<Spec> specWrapper = new LambdaQueryWrapper<>();
        specWrapper.in(Spec::getSpecId, specIds)
                .eq(Spec::getStatus, 1)
                .orderByAsc(Spec::getSortOrder);
        List<Spec> specs = specMapper.selectList(specWrapper);
        
        // 查询规格值
        LambdaQueryWrapper<SpecValue> specValueWrapper = new LambdaQueryWrapper<>();
        specValueWrapper.in(SpecValue::getSpecId, specIds)
                .eq(SpecValue::getStatus, 1)
                .orderByAsc(SpecValue::getSortOrder);
        List<SpecValue> specValues = specValueMapper.selectList(specValueWrapper);
        
        // 按规格ID分组规格值
        Map<Integer, List<SpecValue>> specValueMap = specValues.stream()
                .collect(Collectors.groupingBy(SpecValue::getSpecId));
        
        // 按商品ID分组规格
        Map<Integer, List<ProductSpec>> productSpecMap = productSpecs.stream()
                .collect(Collectors.groupingBy(ProductSpec::getProductId));
        
        // 组装带规格信息的商品DTO
        List<ProductWithSpecsDTO> result = new ArrayList<>();
        for (Product product : orderedProducts) {
            ProductWithSpecsDTO dto = convertToDTO(product);
            
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
            
            dto.setSpecs(productSpecVOs);
            result.add(dto);
        }
        
        log.info("成功获取带规格信息的热销商品排行榜，共 {} 个商品", result.size());
        return result;
    }
    
    /**
     * 将Product实体转换为ProductWithSpecsDTO
     */
    private ProductWithSpecsDTO convertToDTO(Product product) {
        ProductWithSpecsDTO dto = new ProductWithSpecsDTO();
        BeanUtils.copyProperties(product, dto);
        return dto;
    }
}




