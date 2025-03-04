package com.han.cafe.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.han.cafe.entity.Product;
import com.han.cafe.mapper.ProductMapper;
import com.han.cafe.service.ProductService;

/**
* @author MuY1eee
* @description 针对表【product(商品主表)】的数据库操作Service实现
* @createDate 2025-03-04 20:14:41
*/
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product>
    implements ProductService{

}




