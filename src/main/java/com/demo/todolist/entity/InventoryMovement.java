package com.demo.todolist.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@TableName("test.inventory_movements")
public class InventoryMovement {
    private Long movementId;
    private Long productId;
    private String movementType;
    private Integer qty;
    private String warehouse;
    private OffsetDateTime movedAt;
    private String refNote;
}
