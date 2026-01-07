package com.demo.todolist.controller;

import com.demo.todolist.dto.AdminLoginRequest;
import com.demo.todolist.dto.AdminLoginResponse;
import com.demo.todolist.service.AdminAuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/admin")
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    public AdminAuthController(AdminAuthService adminAuthService) {
        this.adminAuthService = adminAuthService;
    }

    @PostMapping("/login")
    public ResponseEntity<AdminLoginResponse> login(@Valid @RequestBody AdminLoginRequest req) {
        AdminAuthService.AuthResult result = adminAuthService.authenticate(req.username(), req.password());

        return switch (result) {
            case OK -> ResponseEntity.ok(new AdminLoginResponse(true, "admin login ok"));
            case NOT_ADMIN -> ResponseEntity.status(HttpStatus.FORBIDDEN).body(new AdminLoginResponse(false, "not admin"));
            case INVALID_CREDENTIALS -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AdminLoginResponse(false, "invalid username or password"));
        };
    }
}
