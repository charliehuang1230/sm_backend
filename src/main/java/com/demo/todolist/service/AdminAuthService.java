package com.demo.todolist.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.demo.todolist.entity.AppUser;
import com.demo.todolist.mapper.AppUserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminAuthService {

    private final AppUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public AdminAuthService(AppUserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResult authenticate(String username, String password) {
        AppUser user = userMapper.selectOne(
                new LambdaQueryWrapper<AppUser>()
                        .eq(AppUser::getUsername, username)
                        .last("LIMIT 1")
        );

        if (user == null) {
            return AuthResult.INVALID_CREDENTIALS;
        }

        String hash = user.getPasswordHash();
        boolean passwordOk = passwordEncoder.matches(password, hash) || password.equals(hash);

        if (!passwordOk) {
            return AuthResult.INVALID_CREDENTIALS;
        }

        if (user.getIsAdmin() == null || !user.getIsAdmin()) {
            return AuthResult.NOT_ADMIN;
        }

        return AuthResult.OK;
    }

    public enum AuthResult {
        OK,
        INVALID_CREDENTIALS,
        NOT_ADMIN
    }
}
