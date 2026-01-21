package com.demo.todolist.customdynamic.controller;

import com.demo.todolist.customdynamic.dto.CustomDynamicCloseAllResponse;
import com.demo.todolist.customdynamic.dto.CustomDynamicCloseResponse;
import com.demo.todolist.customdynamic.dto.CustomDynamicConnectRequest;
import com.demo.todolist.customdynamic.dto.CustomDynamicConnectResponse;
import com.demo.todolist.customdynamic.dto.CustomDynamicConnectionListResponse;
import com.demo.todolist.customdynamic.service.CustomDynamicDataSourceRegistry;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import jakarta.validation.Valid;
import com.demo.todolist.customdynamic.dto.CustomDynamicConnectionInfo;
import java.util.List;

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

    @GetMapping("/connections")
    public ResponseEntity<CustomDynamicConnectionListResponse> listConnections() {
        List<CustomDynamicConnectionInfo> connections = registry.getConnectionInfos();
        return ResponseEntity.ok(new CustomDynamicConnectionListResponse(connections, connections.size()));
    }

    @PostMapping("/close-all")
    public ResponseEntity<CustomDynamicCloseAllResponse> closeAll() {
        int closedCount = registry.removeAll();
        return ResponseEntity.ok(new CustomDynamicCloseAllResponse("ok", "connections closed", closedCount));
    }
}
