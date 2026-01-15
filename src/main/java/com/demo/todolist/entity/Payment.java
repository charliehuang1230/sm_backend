package com.demo.todolist.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@TableName("test.payments")
public class Payment {
    private Long paymentId;
    private Long orderId;
    private String paymentMethod;
    private String paymentStatus;
    private BigDecimal paidAmount;
    private OffsetDateTime paidAt;
}
