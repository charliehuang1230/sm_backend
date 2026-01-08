package com.demo.todolist.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DynamicConnectRequest(
        @NotNull DbType dbType,
        @NotBlank String host,
        @Min(1) @Max(65535) int port,
        @NotBlank String database,
        @NotBlank String username,
        @NotBlank String password
) {
}
