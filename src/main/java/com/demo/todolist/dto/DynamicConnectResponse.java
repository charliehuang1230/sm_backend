package com.demo.todolist.dto;

import java.time.Instant;

public record DynamicConnectResponse(
        String connectionId,
        Instant expiresAt
) {
}
