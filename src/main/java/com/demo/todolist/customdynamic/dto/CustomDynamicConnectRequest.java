package com.demo.todolist.customdynamic.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomDynamicConnectRequest {
    @NotBlank
    private String databaseName;

    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
