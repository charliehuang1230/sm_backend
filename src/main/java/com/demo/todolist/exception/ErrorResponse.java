package com.demo.todolist.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.Map;

/**
 * 統一錯誤回應格式
 * 用於全局異常處理器返回標準化的錯誤訊息
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
    int status,
    String message,
    Map<String, String> details,
    String path,
    Instant timestamp
) {
    /**
     * 簡化構造函數 - 無詳細錯誤訊息
     */
    public ErrorResponse(int status, String message, Instant timestamp) {
        this(status, message, null, null, timestamp);
    }

    /**
     * 簡化構造函數 - 有詳細錯誤訊息
     */
    public ErrorResponse(int status, String message, Map<String, String> details, Instant timestamp) {
        this(status, message, details, null, timestamp);
    }

    /**
     * 簡化構造函數 - 有請求路徑
     */
    public ErrorResponse(int status, String message, String path, Instant timestamp) {
        this(status, message, null, path, timestamp);
    }
}
