package com.demo.todolist.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@TableName("test.orders")
public class OrderEntity {
    private Long orderId;
    private Long customerId;
    private String orderStatus;
    private String orderChannel;
    private String currency;
    private BigDecimal discountAmt;
    private BigDecimal shippingFee;
    private OffsetDateTime createdAt;
    private OffsetDateTime paidAt;
    private OffsetDateTime shippedAt;
}
