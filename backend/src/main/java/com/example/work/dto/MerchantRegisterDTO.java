package com.example.work.dto;

import lombok.Data;

@Data
public class MerchantRegisterDTO {
    // 商户名称
    private String merchant_name;
    // 联系人姓名
    private String contact_name;
    // 联系人手机号
    private String contact_phone;
    // 邮箱
    private String email;
    // 登录账号
    private String username;
    // 登录密码
    private String password;
}

