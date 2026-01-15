package com.demo.todolist.customdynamic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CustomDynamicConnectRequest(
        @NotNull DbType dbType,
        @NotBlank String host,
        @Positive int port,
        @NotBlank String database,
        Boolean useServiceName,
        @NotBlank String username,
        @NotBlank String password
) {
}
