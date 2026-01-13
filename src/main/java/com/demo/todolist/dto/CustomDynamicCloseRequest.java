package com.demo.todolist.dto;

import jakarta.validation.constraints.NotBlank;

public record CustomDynamicCloseRequest(
        @NotBlank String connectionId
) {
}
