package com.demo.todolist.dto;

import java.time.Instant;

public record CustomDynamicConnectResponse(
        String connectionId,
        Instant expiresAt
) {
}
