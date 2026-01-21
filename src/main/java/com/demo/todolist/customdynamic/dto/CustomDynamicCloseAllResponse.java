package com.demo.todolist.customdynamic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomDynamicCloseAllResponse {
    private String status;
    private String message;
    private int closedCount;
}
