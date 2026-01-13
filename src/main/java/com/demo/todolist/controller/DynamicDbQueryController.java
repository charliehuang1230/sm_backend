package com.demo.todolist.controller;

import com.demo.todolist.dto.DynamicProductQueryRequest;
import com.demo.todolist.entity.Product;
import com.demo.todolist.service.DynamicCommerceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/db")
public class DynamicDbQueryController {

    private final DynamicCommerceService commerceService;

    public DynamicDbQueryController(DynamicCommerceService commerceService) {
        this.commerceService = commerceService;
    }

    @PostMapping("/products")
    public ResponseEntity<List<Product>> queryProducts(@Valid @RequestBody DynamicProductQueryRequest request) {
        return ResponseEntity.ok(commerceService.queryProducts(request));
    }
}
