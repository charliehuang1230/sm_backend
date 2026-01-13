package com.demo.todolist.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.time.OffsetDateTime;

public record CustomDynamicReturnQueryRequest(
        @NotBlank String connectionId,
        Long orderId,
        Long productId,
        String returnStatus,
        OffsetDateTime requestedAfter,
        OffsetDateTime requestedBefore,
        @Min(1) @Max(200) Integer limit
) {
}
