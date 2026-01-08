package com.demo.todolist.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.time.OffsetDateTime;

public record DynamicPaymentQueryRequest(
        @NotBlank String connectionId,
        Long orderId,
        String paymentMethod,
        String paymentStatus,
        OffsetDateTime paidAfter,
        OffsetDateTime paidBefore,
        @Min(1) @Max(200) Integer limit
) {
}
