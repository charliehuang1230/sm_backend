package com.demo.todolist.customdynamic.dto;

import jakarta.validation.constraints.NotBlank;

public record CustomDynamicCloseRequest(
        @NotBlank String connectionId
) {
}
