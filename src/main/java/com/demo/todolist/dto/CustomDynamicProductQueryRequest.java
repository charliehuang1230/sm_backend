package com.demo.todolist.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record CustomDynamicProductQueryRequest(
        @NotBlank String connectionId,
        String sku,
        String productName,
        Long categoryId,
        Boolean isActive,
        BigDecimal minListPrice,
        BigDecimal maxListPrice,
        @Min(1) @Max(200) Integer limit
) {
}
