package com.demo.todolist.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CustomDynamicProductQueryRequest(
        @NotBlank String connectionId,
        String sku,
        String productName,
        Long categoryId,
        Boolean isActive,
        BigDecimal minListPrice,
        BigDecimal maxListPrice,
        @Positive Integer limit
) {
}
