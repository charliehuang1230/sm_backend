package com.demo.todolist.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@TableName("products")
public class Product {
    private Long productId;
    private String sku;
    private String productName;
    private Long categoryId;
    private BigDecimal listPrice;
    private BigDecimal costPrice;
    private Boolean isActive;
    private OffsetDateTime createdAt;
}
