package com.demo.todolist.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("app_user")
public class AppUser {
    private Long id;
    private String username;
    private String passwordHash;
    private Boolean isAdmin;
}
