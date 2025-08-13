package com.example.work.entity.master;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminUser {
    private Long id;
    private String username;
    private String password;
    private String role;
    private LocalDateTime createTime;
}