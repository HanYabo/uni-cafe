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
import com.han.cafe.vo.CategoryProductVO;
import com.han.cafe.vo.ProductSpecVO;
import com.han.cafe.vo.ProductVO;
import com.han.cafe.vo.SpecValueVO;

import jakarta.annotation.Resource;

/**
* @author MuY1eee
* @description 针对表【category(商品分类表)】的数据库操作Service实现
* @createDate 2025-03-04 20:14:37
*/
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
}




