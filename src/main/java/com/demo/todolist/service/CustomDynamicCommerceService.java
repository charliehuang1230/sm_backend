package com.demo.todolist.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demo.todolist.customdynamic.config.CustomDynamicDataSourceContext;
import com.demo.todolist.customdynamic.service.CustomDynamicDataSourceRegistry;
import com.demo.todolist.dto.CustomDynamicProductQueryRequest;
import com.demo.todolist.entity.Product;
import com.demo.todolist.mapper.ProductMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;

/**
 * 動態商務查詢服務
 * 簡化版：只處理 Product 相關查詢
 */
@Service
public class CustomDynamicCommerceService {

    private static final int DEFAULT_LIMIT = 50;

    private final CustomDynamicDataSourceRegistry registry;
    private final ProductMapper productMapper;

    public CustomDynamicCommerceService(CustomDynamicDataSourceRegistry registry,
                                  ProductMapper productMapper) {
        this.registry = registry;
        this.productMapper = productMapper;
    }

    /**
     * 查詢產品
     * @param request 查詢請求（包含連接 ID 和查詢條件）
     * @return 產品列表
     */
    public List<Product> queryProducts(CustomDynamicProductQueryRequest request) {
        return withConnection(request.connectionId(), () -> {
            LambdaQueryWrapper<Product> query = new LambdaQueryWrapper<>();

            // 構建查詢條件
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

            // 分頁查詢
            Page<Product> page = new Page<>(1, limitOrDefault(request.limit()));
            return productMapper.selectPage(page, query).getRecords();
        });
    }

    /**
     * 連接上下文管理
     * 確保在正確的數據源上執行查詢，並在查詢後自動清理
     */
    private <T> T withConnection(String connectionId, Supplier<T> supplier) {
        registry.ensureExists(connectionId);
        registry.touch(connectionId);
        CustomDynamicDataSourceContext.setCurrentKey(connectionId);
        try {
            return supplier.get();
        } finally {
            CustomDynamicDataSourceContext.clear();
            // Auto-close connection after query execution (wen_test optimization)
            registry.remove(connectionId);
        }
    }

    /**
     * 限制或使用默認值
     */
    private int limitOrDefault(Integer limit) {
        return limit == null ? DEFAULT_LIMIT : limit;
    }

    /**
     * 檢查字符串是否不為空
     */
    private boolean notBlank(String value) {
        return value != null && !value.isBlank();
    }
}
