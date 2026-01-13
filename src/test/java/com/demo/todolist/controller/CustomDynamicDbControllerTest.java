package com.demo.todolist.controller;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusLanguageDriverAutoConfiguration;
import com.demo.todolist.dto.CustomDynamicConnectRequest;
import com.demo.todolist.dto.CustomDynamicConnectResponse;
import com.demo.todolist.mapper.CategoryMapper;
import com.demo.todolist.mapper.CustomerMapper;
import com.demo.todolist.mapper.InventoryMovementMapper;
import com.demo.todolist.mapper.OrderEntityMapper;
import com.demo.todolist.mapper.OrderItemMapper;
import com.demo.todolist.mapper.PaymentMapper;
import com.demo.todolist.mapper.ProductMapper;
import com.demo.todolist.mapper.ReturnEntryMapper;
import com.demo.todolist.service.CustomDynamicCommerceService;
import com.demo.todolist.service.CustomDynamicDataSourceRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {CustomDynamicDbController.class, CustomDynamicDbQueryController.class},
        excludeAutoConfiguration = {MybatisPlusAutoConfiguration.class, MybatisPlusLanguageDriverAutoConfiguration.class})
@ImportAutoConfiguration(exclude = {MybatisPlusAutoConfiguration.class, MybatisPlusLanguageDriverAutoConfiguration.class})
class CustomDynamicDbControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomDynamicDataSourceRegistry registry;

    @MockBean
    private CustomDynamicCommerceService commerceService;

    @MockBean private CategoryMapper categoryMapper;
    @MockBean private CustomerMapper customerMapper;
    @MockBean private ProductMapper productMapper;
    @MockBean private InventoryMovementMapper inventoryMovementMapper;
    @MockBean private OrderEntityMapper orderEntityMapper;
    @MockBean private OrderItemMapper orderItemMapper;
    @MockBean private PaymentMapper paymentMapper;
    @MockBean private ReturnEntryMapper returnEntryMapper;

    private CustomDynamicConnectResponse connectResponse;

    @BeforeEach
    void setUp() {
        connectResponse = new CustomDynamicConnectResponse("conn-123", Instant.parse("2025-01-01T00:00:00Z"));
    }

    @Test
    void connect_shouldReturnConnectionIdAndExpiry() throws Exception {
        when(registry.connect(org.mockito.ArgumentMatchers.any(CustomDynamicConnectRequest.class)))
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

}
