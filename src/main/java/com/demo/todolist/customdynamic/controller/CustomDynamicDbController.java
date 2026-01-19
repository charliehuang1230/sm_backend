package com.demo.todolist.customdynamic.controller;

import com.demo.todolist.customdynamic.dto.CustomDynamicCloseResponse;
import com.demo.todolist.customdynamic.dto.CustomDynamicConnectRequest;
import com.demo.todolist.customdynamic.dto.CustomDynamicConnectResponse;
import com.demo.todolist.customdynamic.service.CustomDynamicDataSourceRegistry;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/db")
@Validated
public class CustomDynamicDbController {

    private final CustomDynamicDataSourceRegistry registry;

    public CustomDynamicDbController(CustomDynamicDataSourceRegistry registry) {
        this.registry = registry;
    }

    @PostMapping(value = "/connect", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomDynamicConnectResponse> connect(@Valid @ModelAttribute CustomDynamicConnectRequest request) {
        return ResponseEntity.ok(registry.connect(request));
    }

    @PostMapping(value = "/close", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomDynamicCloseResponse> close(@Valid @ModelAttribute CustomDynamicCloseRequest request) {
        registry.ensureExists(request.getConnectionId());
        registry.remove(request.getConnectionId());
        return ResponseEntity.ok(new CustomDynamicCloseResponse("ok", "connection closed"));
    }
}
