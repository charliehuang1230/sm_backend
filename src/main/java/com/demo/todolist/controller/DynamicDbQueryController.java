package com.demo.todolist.controller;

import com.demo.todolist.dto.DynamicAppUserQueryRequest;
import com.demo.todolist.dto.DynamicCategoryQueryRequest;
import com.demo.todolist.dto.DynamicCustomerQueryRequest;
import com.demo.todolist.dto.DynamicInventoryMovementQueryRequest;
import com.demo.todolist.dto.DynamicOrderItemQueryRequest;
import com.demo.todolist.dto.DynamicOrderQueryRequest;
import com.demo.todolist.dto.DynamicPaymentQueryRequest;
import com.demo.todolist.dto.DynamicProductQueryRequest;
import com.demo.todolist.dto.DynamicReturnQueryRequest;
import com.demo.todolist.entity.AppUser;
import com.demo.todolist.entity.Category;
import com.demo.todolist.entity.Customer;
import com.demo.todolist.entity.InventoryMovement;
import com.demo.todolist.entity.OrderEntity;
import com.demo.todolist.entity.OrderItem;
import com.demo.todolist.entity.Payment;
import com.demo.todolist.entity.Product;
import com.demo.todolist.entity.ReturnEntry;
import com.demo.todolist.service.DynamicAppUserService;
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

    private final DynamicAppUserService appUserService;
    private final DynamicCommerceService commerceService;

    public DynamicDbQueryController(DynamicAppUserService appUserService,
                                    DynamicCommerceService commerceService) {
        this.appUserService = appUserService;
        this.commerceService = commerceService;
    }

    @PostMapping("/users")
    public ResponseEntity<List<AppUser>> queryUsers(@Valid @RequestBody DynamicAppUserQueryRequest request) {
        return ResponseEntity.ok(appUserService.queryUsers(request));
    }

    @PostMapping("/customers")
    public ResponseEntity<List<Customer>> queryCustomers(@Valid @RequestBody DynamicCustomerQueryRequest request) {
        return ResponseEntity.ok(commerceService.queryCustomers(request));
    }

    @PostMapping("/categories")
    public ResponseEntity<List<Category>> queryCategories(@Valid @RequestBody DynamicCategoryQueryRequest request) {
        return ResponseEntity.ok(commerceService.queryCategories(request));
    }

    @PostMapping("/products")
    public ResponseEntity<List<Product>> queryProducts(@Valid @RequestBody DynamicProductQueryRequest request) {
        return ResponseEntity.ok(commerceService.queryProducts(request));
    }

    @PostMapping("/inventory-movements")
    public ResponseEntity<List<InventoryMovement>> queryInventoryMovements(@Valid @RequestBody DynamicInventoryMovementQueryRequest request) {
        return ResponseEntity.ok(commerceService.queryInventoryMovements(request));
    }

    @PostMapping("/orders")
    public ResponseEntity<List<OrderEntity>> queryOrders(@Valid @RequestBody DynamicOrderQueryRequest request) {
        return ResponseEntity.ok(commerceService.queryOrders(request));
    }

    @PostMapping("/order-items")
    public ResponseEntity<List<OrderItem>> queryOrderItems(@Valid @RequestBody DynamicOrderItemQueryRequest request) {
        return ResponseEntity.ok(commerceService.queryOrderItems(request));
    }

    @PostMapping("/payments")
    public ResponseEntity<List<Payment>> queryPayments(@Valid @RequestBody DynamicPaymentQueryRequest request) {
        return ResponseEntity.ok(commerceService.queryPayments(request));
    }

    @PostMapping("/returns")
    public ResponseEntity<List<ReturnEntry>> queryReturns(@Valid @RequestBody DynamicReturnQueryRequest request) {
        return ResponseEntity.ok(commerceService.queryReturns(request));
    }
}
