package com.demo.todolist.customdynamic.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomDynamicConnectResponse {
    private String connectionId;
    private Instant expiresAt;
}
