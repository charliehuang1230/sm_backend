package com.demo.todolist.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demo.todolist.config.DynamicDataSourceContext;
import com.demo.todolist.dto.DynamicCategoryQueryRequest;
import com.demo.todolist.dto.DynamicCustomerQueryRequest;
import com.demo.todolist.dto.DynamicInventoryMovementQueryRequest;
import com.demo.todolist.dto.DynamicOrderItemQueryRequest;
import com.demo.todolist.dto.DynamicOrderQueryRequest;
import com.demo.todolist.dto.DynamicPaymentQueryRequest;
import com.demo.todolist.dto.DynamicProductQueryRequest;
import com.demo.todolist.dto.DynamicReturnQueryRequest;
import com.demo.todolist.entity.Category;
import com.demo.todolist.entity.Customer;
import com.demo.todolist.entity.InventoryMovement;
import com.demo.todolist.entity.OrderEntity;
import com.demo.todolist.entity.OrderItem;
import com.demo.todolist.entity.Payment;
import com.demo.todolist.entity.Product;
import com.demo.todolist.entity.ReturnEntry;
import com.demo.todolist.mapper.CategoryMapper;
import com.demo.todolist.mapper.CustomerMapper;
import com.demo.todolist.mapper.InventoryMovementMapper;
import com.demo.todolist.mapper.OrderEntityMapper;
import com.demo.todolist.mapper.OrderItemMapper;
import com.demo.todolist.mapper.PaymentMapper;
import com.demo.todolist.mapper.ProductMapper;
import com.demo.todolist.mapper.ReturnEntryMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;

@Service
public class DynamicCommerceService {

    private static final int DEFAULT_LIMIT = 50;

    private final DynamicDataSourceRegistry registry;
    private final CustomerMapper customerMapper;
    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;
    private final InventoryMovementMapper inventoryMovementMapper;
    private final OrderEntityMapper orderEntityMapper;
    private final OrderItemMapper orderItemMapper;
    private final PaymentMapper paymentMapper;
    private final ReturnEntryMapper returnEntryMapper;

    public DynamicCommerceService(DynamicDataSourceRegistry registry,
                                  CustomerMapper customerMapper,
                                  CategoryMapper categoryMapper,
                                  ProductMapper productMapper,
                                  InventoryMovementMapper inventoryMovementMapper,
                                  OrderEntityMapper orderEntityMapper,
                                  OrderItemMapper orderItemMapper,
                                  PaymentMapper paymentMapper,
                                  ReturnEntryMapper returnEntryMapper) {
        this.registry = registry;
        this.customerMapper = customerMapper;
        this.categoryMapper = categoryMapper;
        this.productMapper = productMapper;
        this.inventoryMovementMapper = inventoryMovementMapper;
        this.orderEntityMapper = orderEntityMapper;
        this.orderItemMapper = orderItemMapper;
        this.paymentMapper = paymentMapper;
        this.returnEntryMapper = returnEntryMapper;
    }

    public List<Customer> queryCustomers(DynamicCustomerQueryRequest request) {
        return withConnection(request.connectionId(), () -> {
            LambdaQueryWrapper<Customer> query = new LambdaQueryWrapper<>();
            if (notBlank(request.email())) {
                query.eq(Customer::getEmail, request.email());
            }
            if (notBlank(request.fullName())) {
                query.like(Customer::getFullName, request.fullName());
            }
            if (notBlank(request.country())) {
                query.eq(Customer::getCountry, request.country());
            }
            if (notBlank(request.city())) {
                query.eq(Customer::getCity, request.city());
            }
            if (request.isVip() != null) {
                query.eq(Customer::getIsVip, request.isVip());
            }
            if (request.signupAfter() != null) {
                query.ge(Customer::getSignupAt, request.signupAfter());
            }
            if (request.signupBefore() != null) {
                query.le(Customer::getSignupAt, request.signupBefore());
            }
            Page<Customer> page = new Page<>(1, limitOrDefault(request.limit()));
            return customerMapper.selectPage(page, query).getRecords();
        });
    }

    public List<Category> queryCategories(DynamicCategoryQueryRequest request) {
        return withConnection(request.connectionId(), () -> {
            LambdaQueryWrapper<Category> query = new LambdaQueryWrapper<>();
            if (notBlank(request.categoryName())) {
                query.like(Category::getCategoryName, request.categoryName());
            }
            Page<Category> page = new Page<>(1, limitOrDefault(request.limit()));
            return categoryMapper.selectPage(page, query).getRecords();
        });
    }

    public List<Product> queryProducts(DynamicProductQueryRequest request) {
        return withConnection(request.connectionId(), () -> {
            LambdaQueryWrapper<Product> query = new LambdaQueryWrapper<>();
            if (notBlank(request.sku())) {
                query.eq(Product::getSku, request.sku());
            }
            if (notBlank(request.productName())) {
                query.like(Product::getProductName, request.productName());
            }
            if (request.categoryId() != null) {
                query.eq(Product::getCategoryId, request.categoryId());
            }
            if (request.isActive() != null) {
                query.eq(Product::getIsActive, request.isActive());
            }
            if (request.minListPrice() != null) {
                query.ge(Product::getListPrice, request.minListPrice());
            }
            if (request.maxListPrice() != null) {
                query.le(Product::getListPrice, request.maxListPrice());
            }
            Page<Product> page = new Page<>(1, limitOrDefault(request.limit()));
            return productMapper.selectPage(page, query).getRecords();
        });
    }

    public List<InventoryMovement> queryInventoryMovements(DynamicInventoryMovementQueryRequest request) {
        return withConnection(request.connectionId(), () -> {
            LambdaQueryWrapper<InventoryMovement> query = new LambdaQueryWrapper<>();
            if (request.productId() != null) {
                query.eq(InventoryMovement::getProductId, request.productId());
            }
            if (notBlank(request.movementType())) {
                query.eq(InventoryMovement::getMovementType, request.movementType());
            }
            if (notBlank(request.warehouse())) {
                query.eq(InventoryMovement::getWarehouse, request.warehouse());
            }
            if (request.movedAfter() != null) {
                query.ge(InventoryMovement::getMovedAt, request.movedAfter());
            }
            if (request.movedBefore() != null) {
                query.le(InventoryMovement::getMovedAt, request.movedBefore());
            }
            Page<InventoryMovement> page = new Page<>(1, limitOrDefault(request.limit()));
            return inventoryMovementMapper.selectPage(page, query).getRecords();
        });
    }

    public List<OrderEntity> queryOrders(DynamicOrderQueryRequest request) {
        return withConnection(request.connectionId(), () -> {
            LambdaQueryWrapper<OrderEntity> query = new LambdaQueryWrapper<>();
            if (request.customerId() != null) {
                query.eq(OrderEntity::getCustomerId, request.customerId());
            }
            if (notBlank(request.orderStatus())) {
                query.eq(OrderEntity::getOrderStatus, request.orderStatus());
            }
            if (notBlank(request.orderChannel())) {
                query.eq(OrderEntity::getOrderChannel, request.orderChannel());
            }
            if (request.createdAfter() != null) {
                query.ge(OrderEntity::getCreatedAt, request.createdAfter());
            }
            if (request.createdBefore() != null) {
                query.le(OrderEntity::getCreatedAt, request.createdBefore());
            }
            Page<OrderEntity> page = new Page<>(1, limitOrDefault(request.limit()));
            return orderEntityMapper.selectPage(page, query).getRecords();
        });
    }

    public List<OrderItem> queryOrderItems(DynamicOrderItemQueryRequest request) {
        return withConnection(request.connectionId(), () -> {
            LambdaQueryWrapper<OrderItem> query = new LambdaQueryWrapper<>();
            if (request.orderId() != null) {
                query.eq(OrderItem::getOrderId, request.orderId());
            }
            if (request.productId() != null) {
                query.eq(OrderItem::getProductId, request.productId());
            }
            Page<OrderItem> page = new Page<>(1, limitOrDefault(request.limit()));
            return orderItemMapper.selectPage(page, query).getRecords();
        });
    }

    public List<Payment> queryPayments(DynamicPaymentQueryRequest request) {
        return withConnection(request.connectionId(), () -> {
            LambdaQueryWrapper<Payment> query = new LambdaQueryWrapper<>();
            if (request.orderId() != null) {
                query.eq(Payment::getOrderId, request.orderId());
            }
            if (notBlank(request.paymentMethod())) {
                query.eq(Payment::getPaymentMethod, request.paymentMethod());
            }
            if (notBlank(request.paymentStatus())) {
                query.eq(Payment::getPaymentStatus, request.paymentStatus());
            }
            if (request.paidAfter() != null) {
                query.ge(Payment::getPaidAt, request.paidAfter());
            }
            if (request.paidBefore() != null) {
                query.le(Payment::getPaidAt, request.paidBefore());
            }
            Page<Payment> page = new Page<>(1, limitOrDefault(request.limit()));
            return paymentMapper.selectPage(page, query).getRecords();
        });
    }

    public List<ReturnEntry> queryReturns(DynamicReturnQueryRequest request) {
        return withConnection(request.connectionId(), () -> {
            LambdaQueryWrapper<ReturnEntry> query = new LambdaQueryWrapper<>();
            if (request.orderId() != null) {
                query.eq(ReturnEntry::getOrderId, request.orderId());
            }
            if (request.productId() != null) {
                query.eq(ReturnEntry::getProductId, request.productId());
            }
            if (notBlank(request.returnStatus())) {
                query.eq(ReturnEntry::getReturnStatus, request.returnStatus());
            }
            if (request.requestedAfter() != null) {
                query.ge(ReturnEntry::getRequestedAt, request.requestedAfter());
            }
            if (request.requestedBefore() != null) {
                query.le(ReturnEntry::getRequestedAt, request.requestedBefore());
            }
            Page<ReturnEntry> page = new Page<>(1, limitOrDefault(request.limit()));
            return returnEntryMapper.selectPage(page, query).getRecords();
        });
    }

    private <T> T withConnection(String connectionId, Supplier<T> supplier) {
        registry.ensureExists(connectionId);
        registry.touch(connectionId);
        DynamicDataSourceContext.setCurrentKey(connectionId);
        try {
            return supplier.get();
        } finally {
            DynamicDataSourceContext.clear();
        }
    }

    private int limitOrDefault(Integer limit) {
        return limit == null ? DEFAULT_LIMIT : limit;
    }

    private boolean notBlank(String value) {
        return value != null && !value.isBlank();
    }
}
