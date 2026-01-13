package com.demo.todolist.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DynamicConnectRequest(
        @NotNull DbType dbType,
        @NotBlank String host,
        @Positive int port,
        @NotBlank String database,
        @NotBlank String username,
        @NotBlank String password
) {
}
