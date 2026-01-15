package com.demo.todolist.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demo.todolist.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
}
