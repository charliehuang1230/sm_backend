package com.demo.todolist.controller;

import com.demo.todolist.dto.DynamicConnectRequest;
import com.demo.todolist.dto.DynamicConnectResponse;
import com.demo.todolist.dto.DynamicProductQueryRequest;
import com.demo.todolist.entity.Product;
import com.demo.todolist.mapper.ProductMapper;
import com.demo.todolist.service.DynamicCommerceService;
import com.demo.todolist.service.DynamicDataSourceRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {DynamicDbController.class, DynamicDbQueryController.class})
@TestPropertySource(properties = {"mybatis-plus.enabled=false"})
class DynamicDbControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DynamicDataSourceRegistry registry;

    @MockBean
    private DynamicCommerceService commerceService;

    @MockBean
    private ProductMapper productMapper;

    private DynamicConnectResponse connectResponse;

    @BeforeEach
    void setUp() {
        connectResponse = new DynamicConnectResponse("conn-123", Instant.parse("2025-01-01T00:00:00Z"));
    }

    @Test
    void connect_shouldReturnConnectionIdAndExpiry() throws Exception {
        when(registry.connect(org.mockito.ArgumentMatchers.any(DynamicConnectRequest.class)))
                .thenReturn(connectResponse);

        String body = """
                {
                  "dbType": "POSTGRES",
                  "host": "localhost",
                  "port": 5432,
                  "database": "ssm_test",
                  "username": "demo",
                  "password": "secret"
                }
                """;

        mockMvc.perform(post("/api/db/connect")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(connectResponse)));
    }

    @Test
    void close_shouldInvokeRegistryAndReturn204() throws Exception {
        String body = """
                {
                  "connectionId": "conn-123"
                }
                """;

        mockMvc.perform(post("/api/db/close")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNoContent());

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(registry).ensureExists(captor.capture());
        verify(registry).remove("conn-123");
        assertThat(captor.getValue()).isEqualTo("conn-123");
    }

    @Test
    void products_shouldReturnListFromService() throws Exception {
        Product product = new Product();
        product.setProductId(1L);
        product.setSku("TEST-001");
        product.setProductName("Test Product");
        product.setCategoryId(1L);
        product.setListPrice(new BigDecimal("999.99"));
        product.setCostPrice(new BigDecimal("500.00"));
        product.setIsActive(true);
        product.setCreatedAt(OffsetDateTime.now());

        when(commerceService.queryProducts(org.mockito.ArgumentMatchers.any(DynamicProductQueryRequest.class)))
                .thenReturn(List.of(product));

        String body = """
                {
                  "connectionId": "conn-123",
                  "productName": "Test",
                  "limit": 10
                }
                """;

        mockMvc.perform(post("/api/db/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Collections.singletonList(product))));
    }
}
