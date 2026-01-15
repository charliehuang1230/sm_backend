package com.demo.todolist.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@TableName("test.returns")
public class ReturnEntry {
    private Long returnId;
    private Long orderId;
    private Long productId;
    private Integer qty;
    private String reason;
    private String returnStatus;
    private OffsetDateTime requestedAt;
    private OffsetDateTime resolvedAt;
}
