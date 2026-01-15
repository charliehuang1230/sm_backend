package com.demo.todolist.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demo.todolist.entity.Customer;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {
}
