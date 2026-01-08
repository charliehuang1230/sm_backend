package com.demo.todolist.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.time.OffsetDateTime;

public record DynamicCustomerQueryRequest(
        @NotBlank String connectionId,
        String email,
        String fullName,
        String country,
        String city,
        Boolean isVip,
        OffsetDateTime signupAfter,
        OffsetDateTime signupBefore,
        @Min(1) @Max(200) Integer limit
) {
}
