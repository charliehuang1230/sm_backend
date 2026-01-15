package com.demo.todolist.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("test.order_items")
public class OrderItem {
    private Long orderItemId;
    private Long orderId;
    private Long productId;
    private Integer qty;
    private BigDecimal unitPrice;
    private BigDecimal itemDiscount;
}
