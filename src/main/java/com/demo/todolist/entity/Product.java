package com.demo.todolist.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@TableName("test_dynamic.products")
public class Product {
    @TableId
    private Long productId;
    private String sku;
    private String productName;
    private Long categoryId;
    private BigDecimal listPrice;
    private BigDecimal costPrice;
    private Boolean isActive;
    private OffsetDateTime createdAt;
}
