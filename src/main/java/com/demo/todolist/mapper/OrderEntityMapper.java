package com.demo.todolist.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demo.todolist.entity.OrderEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderEntityMapper extends BaseMapper<OrderEntity> {
}
