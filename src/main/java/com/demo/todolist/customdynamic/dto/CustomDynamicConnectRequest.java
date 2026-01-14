package com.demo.todolist.customdynamic.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CustomDynamicConnectRequest(
        @NotNull DbType dbType,
        @NotBlank String host,
        @Min(1) @Max(65535) int port,
        @NotBlank String database,
        Boolean useServiceName,
        @NotBlank String username,
        @NotBlank String password
) {
}
