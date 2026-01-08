package com.demo.todolist.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.time.OffsetDateTime;

public record DynamicOrderQueryRequest(
        @NotBlank String connectionId,
        Long customerId,
        String orderStatus,
        String orderChannel,
        OffsetDateTime createdAfter,
        OffsetDateTime createdBefore,
        @Min(1) @Max(200) Integer limit
) {
}
