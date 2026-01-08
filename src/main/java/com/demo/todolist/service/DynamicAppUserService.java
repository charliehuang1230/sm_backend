package com.demo.todolist.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demo.todolist.config.DynamicDataSourceContext;
import com.demo.todolist.dto.DynamicAppUserQueryRequest;
import com.demo.todolist.entity.AppUser;
import com.demo.todolist.mapper.AppUserMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DynamicAppUserService {

    private static final int DEFAULT_LIMIT = 50;

    private final AppUserMapper appUserMapper;
    private final DynamicDataSourceRegistry registry;

    public DynamicAppUserService(AppUserMapper appUserMapper, DynamicDataSourceRegistry registry) {
        this.appUserMapper = appUserMapper;
        this.registry = registry;
    }

    public List<AppUser> queryUsers(DynamicAppUserQueryRequest request) {
        registry.ensureExists(request.connectionId());
        registry.touch(request.connectionId());

        DynamicDataSourceContext.setCurrentKey(request.connectionId());
        try {
            LambdaQueryWrapper<AppUser> query = new LambdaQueryWrapper<>();
            if (request.username() != null && !request.username().isBlank()) {
                query.eq(AppUser::getUsername, request.username());
            }
            if (request.isAdmin() != null) {
                query.eq(AppUser::getIsAdmin, request.isAdmin());
            }

            int limit = request.limit() == null ? DEFAULT_LIMIT : request.limit();
            Page<AppUser> page = new Page<>(1, limit);
            return appUserMapper.selectPage(page, query).getRecords();
        } finally {
            DynamicDataSourceContext.clear();
        }
    }
}
