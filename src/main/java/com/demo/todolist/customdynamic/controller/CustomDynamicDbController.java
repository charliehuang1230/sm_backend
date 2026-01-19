package com.demo.todolist.customdynamic.controller;

import com.demo.todolist.customdynamic.dto.CustomDynamicCloseResponse;
import com.demo.todolist.customdynamic.dto.CustomDynamicConnectRequest;
import com.demo.todolist.customdynamic.dto.CustomDynamicConnectResponse;
import com.demo.todolist.customdynamic.service.CustomDynamicDataSourceRegistry;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("api/db")
@Validated
public class CustomDynamicDbController {

    private final CustomDynamicDataSourceRegistry registry;

    public CustomDynamicDbController(CustomDynamicDataSourceRegistry registry) {
        this.registry = registry;
    }

    @PostMapping("/connect")
    public ResponseEntity<CustomDynamicConnectResponse> connect(@RequestParam @NotBlank String databaseName,
                                                                @RequestParam @NotBlank String username,
                                                                @RequestParam @NotBlank String password) {
        CustomDynamicConnectRequest request = new CustomDynamicConnectRequest(databaseName, username, password);
        return ResponseEntity.ok(registry.connect(request));
    }

    @PostMapping("/close")
    public ResponseEntity<CustomDynamicCloseResponse> close(@RequestParam @NotBlank String connectionId) {
        registry.ensureExists(connectionId);
        registry.remove(connectionId);
        return ResponseEntity.ok(new CustomDynamicCloseResponse("ok", "connection closed"));
    }
}
