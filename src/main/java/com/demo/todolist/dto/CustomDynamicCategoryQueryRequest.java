package com.demo.todolist.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CustomDynamicCategoryQueryRequest(
        @NotBlank String connectionId,
        String categoryName,
        @Min(1) @Max(200) Integer limit
) {
}
