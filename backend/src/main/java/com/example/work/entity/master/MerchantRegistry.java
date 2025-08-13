package com.example.work.entity.master;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MerchantRegistry {
    private Long id;
    // 商户ID
    private String merchant_id;
    // 公司名称
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
    // 专属数据库名称
    private String database_name;
    // 注册时间
    private LocalDateTime register_time;
    // 状态 0:禁用 1:正常
    private Integer status;
}
