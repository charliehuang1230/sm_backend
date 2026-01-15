package com.demo.todolist.controller;

import com.demo.todolist.dto.CustomDynamicProductQueryRequest;
import com.demo.todolist.entity.Product;
import com.demo.todolist.service.CustomDynamicCommerceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 動態資料庫查詢控制器
 * 簡化版：只提供 Product 查詢端點
 */
@RestController
@RequestMapping("api/postgres/db")
public class CustomDynamicDbQueryController {

    private final CustomDynamicCommerceService commerceService;

    public CustomDynamicDbQueryController(CustomDynamicCommerceService commerceService) {
        this.commerceService = commerceService;
    }

    /**
     * 查詢產品
     * POST /api/postgres/db/products
     *
     * 請求體範例：
     * {
     *   "connectionId": "abc-123-def",
     *   "sku": "SKU001",
     *   "productName": "Product Name",
     *   "categoryId": 1,
     *   "isActive": true,
     *   "minListPrice": 10.0,
     *   "maxListPrice": 100.0,
     *   "limit": 50
     * }
     *
     * @param request 產品查詢請求
     * @return 產品列表
     */
    @PostMapping("/products")
    public ResponseEntity<List<Product>> queryProducts(@Valid @RequestBody CustomDynamicProductQueryRequest request) {
        return ResponseEntity.ok(commerceService.queryProducts(request));
    }
}
