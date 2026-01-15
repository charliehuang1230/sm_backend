package com.demo.todolist.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * 異常測試控制器
 * 用於測試全局異常處理器的各種異常情況
 *
 * 測試完成後可以刪除此控制器
 */
@RestController
@RequestMapping("/api/test/exception")
public class ExceptionTestController {

    /**
     * 測試 ResponseStatusException (404)
     * GET /api/test/exception/not-found
     */
    @GetMapping("/not-found")
    public ResponseEntity<String> testNotFound() {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "測試資源不存在");
    }

    /**
     * 測試 ResponseStatusException (400)
     * GET /api/test/exception/bad-request
     */
    @GetMapping("/bad-request")
    public ResponseEntity<String> testBadRequest() {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "測試錯誤的請求參數");
    }

    /**
     * 測試 @Valid 參數驗證失敗
     * POST /api/test/exception/validation
     * Body: {"name": "", "age": -1}
     */
    @PostMapping("/validation")
    public ResponseEntity<String> testValidation(@Valid @RequestBody TestRequest request) {
        return ResponseEntity.ok("驗證通過: " + request.name());
    }

    /**
     * 測試 IllegalArgumentException
     * GET /api/test/exception/illegal-argument
     */
    @GetMapping("/illegal-argument")
    public ResponseEntity<String> testIllegalArgument() {
        throw new IllegalArgumentException("測試參數不合法");
    }

    /**
     * 測試未預期的異常
     * GET /api/test/exception/unexpected
     */
    @GetMapping("/unexpected")
    public ResponseEntity<String> testUnexpectedException() {
        throw new RuntimeException("測試未預期的系統錯誤");
    }

    /**
     * 測試 JSON 格式錯誤
     * POST /api/test/exception/json-error
     * Body: {invalid json}
     */
    @PostMapping("/json-error")
    public ResponseEntity<String> testJsonError(@RequestBody String body) {
        return ResponseEntity.ok(body);
    }

    /**
     * 測試路徑變量驗證
     * GET /api/test/exception/path-validation/{id}
     */
    @GetMapping("/path-validation/{id}")
    public ResponseEntity<String> testPathValidation(
            @PathVariable @Min(value = 1, message = "ID 必須大於 0") Integer id) {
        return ResponseEntity.ok("ID: " + id);
    }

    /**
     * 測試請求參數
     */
    public record TestRequest(
        @NotBlank(message = "名稱不能為空")
        String name,

        @Min(value = 0, message = "年齡必須大於等於 0")
        Integer age
    ) {}
}
