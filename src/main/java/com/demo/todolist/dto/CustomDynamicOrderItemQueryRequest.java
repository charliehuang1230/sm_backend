package com.demo.todolist.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CustomDynamicOrderItemQueryRequest(
        @NotBlank String connectionId,
        Long orderId,
        Long productId,
        @Min(1) @Max(200) Integer limit
) {
}
