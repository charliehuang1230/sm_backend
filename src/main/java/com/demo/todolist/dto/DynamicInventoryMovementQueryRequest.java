package com.demo.todolist.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.time.OffsetDateTime;

public record DynamicInventoryMovementQueryRequest(
        @NotBlank String connectionId,
        Long productId,
        String movementType,
        String warehouse,
        OffsetDateTime movedAfter,
        OffsetDateTime movedBefore,
        @Min(1) @Max(200) Integer limit
) {
}
