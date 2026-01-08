package com.demo.todolist.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record DynamicAppUserQueryRequest(
        @NotBlank String connectionId,
        String username,
        Boolean isAdmin,
        @Min(1) @Max(200) Integer limit
) {
}
