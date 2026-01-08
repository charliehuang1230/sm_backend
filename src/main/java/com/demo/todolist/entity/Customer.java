package com.demo.todolist.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@TableName("test.customers")
public class Customer {
    private Long customerId;
    private String email;
    private String fullName;
    private String phone;
    private String country;
    private String city;
    private OffsetDateTime signupAt;
    private Boolean isVip;
}
