package com.example.work.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMerchantRegistryDTO {
    // 商家id
    private String merchant_id;
    // 公司名称
    private String merchant_name;
    // 联系人姓名
    private String contact_name;
    // 联系人手机号
    private String contact_phone;
    // 邮箱
    private String email;
    // 登录密码
    private String password;
    // 状态 0:禁用 1:正常
    private Integer status;
}
