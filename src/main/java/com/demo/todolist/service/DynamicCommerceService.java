package com.demo.todolist.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demo.todolist.config.DynamicDataSourceContext;
import com.demo.todolist.dto.DynamicProductQueryRequest;
import com.demo.todolist.entity.Product;
import com.demo.todolist.mapper.ProductMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;

@Service
public class DynamicCommerceService {

    private static final int DEFAULT_LIMIT = 50;

    private final DynamicDataSourceRegistry registry;
    private final ProductMapper productMapper;

    public DynamicCommerceService(DynamicDataSourceRegistry registry, ProductMapper productMapper) {
        this.registry = registry;
        this.productMapper = productMapper;
    }

    public List<Product> queryProducts(DynamicProductQueryRequest request) {
        return withConnection(request.connectionId(), () -> {
            LambdaQueryWrapper<Product> query = new LambdaQueryWrapper<>();
            if (notBlank(request.sku())) {
                query.eq(Product::getSku, request.sku());
            }
            if (notBlank(request.productName())) {
                query.like(Product::getProductName, request.productName());
            }
            if (request.categoryId() != null) {
                query.eq(Product::getCategoryId, request.categoryId());
            }
            if (request.isActive() != null) {
                query.eq(Product::getIsActive, request.isActive());
            }
            if (request.minListPrice() != null) {
                query.ge(Product::getListPrice, request.minListPrice());
            }
            if (request.maxListPrice() != null) {
                query.le(Product::getListPrice, request.maxListPrice());
            }
            Page<Product> page = new Page<>(1, limitOrDefault(request.limit()));
            return productMapper.selectPage(page, query).getRecords();
        });
    }

    private <T> T withConnection(String connectionId, Supplier<T> supplier) {
        registry.ensureExists(connectionId);
        registry.touch(connectionId);
        DynamicDataSourceContext.setCurrentKey(connectionId);
        try {
            return supplier.get();
        } finally {
            DynamicDataSourceContext.clear();
        }
    }

    private int limitOrDefault(Integer limit) {
        return limit == null ? DEFAULT_LIMIT : limit;
    }

    private boolean notBlank(String value) {
        return value != null && !value.isBlank();
    }
}
