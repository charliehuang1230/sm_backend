package com.demo.todolist.dto;

import jakarta.validation.constraints.NotBlank;

public record DynamicCloseRequest(
        @NotBlank String connectionId
) {
}
