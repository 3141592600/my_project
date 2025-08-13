package com.example.work.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserInfoDTO {
    // 用户账号
    private String username;
    // 用户密码
    private String password;
    // 真实姓名
    private String real_name;
    // 手机号码
    private String phone;
    // 邮箱
    private String email;
}
