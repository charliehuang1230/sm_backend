package com.demo.todolist.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demo.todolist.entity.Product;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}
