package com.demo.todolist.controller;

import com.demo.todolist.dto.CustomDynamicCategoryQueryRequest;
import com.demo.todolist.dto.CustomDynamicCustomerQueryRequest;
import com.demo.todolist.dto.CustomDynamicInventoryMovementQueryRequest;
import com.demo.todolist.dto.CustomDynamicOrderItemQueryRequest;
import com.demo.todolist.dto.CustomDynamicOrderQueryRequest;
import com.demo.todolist.dto.CustomDynamicPaymentQueryRequest;
import com.demo.todolist.dto.CustomDynamicProductQueryRequest;
import com.demo.todolist.dto.CustomDynamicReturnQueryRequest;
import com.demo.todolist.entity.Category;
import com.demo.todolist.entity.Customer;
import com.demo.todolist.entity.InventoryMovement;
import com.demo.todolist.entity.OrderEntity;
import com.demo.todolist.entity.OrderItem;
import com.demo.todolist.entity.Payment;
import com.demo.todolist.entity.Product;
import com.demo.todolist.entity.ReturnEntry;
import com.demo.todolist.service.CustomDynamicCommerceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/db")
public class CustomDynamicDbQueryController {

    private final CustomDynamicCommerceService commerceService;

    public CustomDynamicDbQueryController(CustomDynamicCommerceService commerceService) {
        this.commerceService = commerceService;
    }

    @PostMapping("/customers")
    public ResponseEntity<List<Customer>> queryCustomers(@Valid @RequestBody CustomDynamicCustomerQueryRequest request) {
        return ResponseEntity.ok(commerceService.queryCustomers(request));
    }

    @PostMapping("/categories")
    public ResponseEntity<List<Category>> queryCategories(@Valid @RequestBody CustomDynamicCategoryQueryRequest request) {
        return ResponseEntity.ok(commerceService.queryCategories(request));
    }

    @PostMapping("/products")
    public ResponseEntity<List<Product>> queryProducts(@Valid @RequestBody CustomDynamicProductQueryRequest request) {
        return ResponseEntity.ok(commerceService.queryProducts(request));
    }

    @PostMapping("/inventory-movements")
    public ResponseEntity<List<InventoryMovement>> queryInventoryMovements(@Valid @RequestBody CustomDynamicInventoryMovementQueryRequest request) {
        return ResponseEntity.ok(commerceService.queryInventoryMovements(request));
    }

    @PostMapping("/orders")
    public ResponseEntity<List<OrderEntity>> queryOrders(@Valid @RequestBody CustomDynamicOrderQueryRequest request) {
        return ResponseEntity.ok(commerceService.queryOrders(request));
    }

    @PostMapping("/order-items")
    public ResponseEntity<List<OrderItem>> queryOrderItems(@Valid @RequestBody CustomDynamicOrderItemQueryRequest request) {
        return ResponseEntity.ok(commerceService.queryOrderItems(request));
    }

    @PostMapping("/payments")
    public ResponseEntity<List<Payment>> queryPayments(@Valid @RequestBody CustomDynamicPaymentQueryRequest request) {
        return ResponseEntity.ok(commerceService.queryPayments(request));
    }

    @PostMapping("/returns")
    public ResponseEntity<List<ReturnEntry>> queryReturns(@Valid @RequestBody CustomDynamicReturnQueryRequest request) {
        return ResponseEntity.ok(commerceService.queryReturns(request));
    }
}
