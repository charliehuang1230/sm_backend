package com.demo.todolist.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("test.categories")
public class Category {
    private Long categoryId;
    private String categoryName;
}
