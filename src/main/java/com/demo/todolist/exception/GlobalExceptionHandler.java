package com.demo.todolist.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 全局異常處理器
 * 統一處理所有控制器拋出的異常，提供標準化的錯誤回應格式
 *
 * 功能：
 * 1. 處理 ResponseStatusException（業務異常）
 * 2. 處理參數驗證異常
 * 3. 處理 JSON 解析異常
 * 4. 處理未預期的系統異常
 * 5. 自動記錄所有異常日誌
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 處理 ResponseStatusException
     * 這是應用中主要使用的業務異常類型
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(
            ResponseStatusException ex,
            HttpServletRequest request) {

        int statusCode = ex.getStatusCode().value();
        String reason = ex.getReason() != null ? ex.getReason() : "Request failed";

        // 根據狀態碼決定日誌級別
        if (statusCode >= 500) {
            log.error("Server error [{}] at {}: {}", statusCode, request.getRequestURI(), reason, ex);
        } else if (statusCode == 404) {
            log.warn("Resource not found [{}] at {}: {}", statusCode, request.getRequestURI(), reason);
        } else {
            log.info("Client error [{}] at {}: {}", statusCode, request.getRequestURI(), reason);
        }

        ErrorResponse errorResponse = new ErrorResponse(
            statusCode,
            reason,
            request.getRequestURI(),
            Instant.now()
        );

        return ResponseEntity.status(ex.getStatusCode()).body(errorResponse);
    }

    /**
     * 處理 @Valid 參數驗證失敗異常
     * 當請求體中的參數不符合驗證規則時觸發
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = error instanceof FieldError
                ? ((FieldError) error).getField()
                : error.getObjectName();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.warn("Validation failed at {}: {}", request.getRequestURI(), errors);

        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "參數驗證失敗",
            errors,
            request.getRequestURI(),
            Instant.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * 處理約束驗證異常
     * 當方法參數或路徑變量驗證失敗時觸發
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(
            ConstraintViolationException ex,
            HttpServletRequest request) {

        Map<String, String> errors = ex.getConstraintViolations().stream()
            .collect(Collectors.toMap(
                violation -> violation.getPropertyPath().toString(),
                ConstraintViolation::getMessage
            ));

        log.warn("Constraint violation at {}: {}", request.getRequestURI(), errors);

        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "參數約束驗證失敗",
            errors,
            request.getRequestURI(),
            Instant.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * 處理 JSON 解析異常
     * 當請求體格式不正確或無法解析時觸發
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        log.warn("Invalid JSON format at {}: {}", request.getRequestURI(), ex.getMessage());

        String message = "請求格式錯誤，請檢查 JSON 格式是否正確";

        // 嘗試提取更具體的錯誤訊息
        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("JSON parse error")) {
                message = "JSON 解析失敗，請檢查格式";
            } else if (ex.getMessage().contains("Required request body is missing")) {
                message = "缺少請求體";
            }
        }

        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            message,
            request.getRequestURI(),
            Instant.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * 處理 IllegalArgumentException
     * 當參數不合法時觸發
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        log.warn("Illegal argument at {}: {}", request.getRequestURI(), ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            ex.getMessage() != null ? ex.getMessage() : "參數不合法",
            request.getRequestURI(),
            Instant.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * 處理所有未捕獲的異常
     * 這是最後的防線，確保所有異常都有統一的回應格式
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        // 系統異常需要記錄完整堆疊
        log.error("Unexpected error at {}", request.getRequestURI(), ex);

        // 不要暴露內部錯誤細節給客戶端
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "伺服器內部錯誤，請稍後再試",
            request.getRequestURI(),
            Instant.now()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
