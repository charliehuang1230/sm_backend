package com.demo.todolist.customdynamic.dto;

import java.time.Instant;

public record CustomDynamicConnectResponse(
        String connectionId,
        Instant expiresAt
) {
}
