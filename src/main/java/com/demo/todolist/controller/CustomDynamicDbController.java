package com.demo.todolist.controller;

import com.demo.todolist.dto.CustomDynamicCloseRequest;
import com.demo.todolist.dto.CustomDynamicConnectRequest;
import com.demo.todolist.dto.CustomDynamicConnectResponse;
import com.demo.todolist.service.CustomDynamicDataSourceRegistry;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/db")
public class CustomDynamicDbController {

    private final CustomDynamicDataSourceRegistry registry;

    public CustomDynamicDbController(CustomDynamicDataSourceRegistry registry) {
        this.registry = registry;
    }

    @PostMapping("/connect")
    public ResponseEntity<CustomDynamicConnectResponse> connect(@Valid @RequestBody CustomDynamicConnectRequest request) {
        return ResponseEntity.ok(registry.connect(request));
    }

    @PostMapping("/close")
    public ResponseEntity<Void> close(@Valid @RequestBody CustomDynamicCloseRequest request) {
        registry.ensureExists(request.connectionId());
        registry.remove(request.connectionId());
        return ResponseEntity.noContent().build();
    }
}
