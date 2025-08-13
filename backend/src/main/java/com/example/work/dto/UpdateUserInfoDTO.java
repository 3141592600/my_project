package com.example.work.dto;

import lombok.Data;

@Data
public class UpdateUserInfoDTO {
    // 真实姓名
    private String real_name;
    // 手机号码
    private String phone;
    // 邮箱
    private String email;
}
