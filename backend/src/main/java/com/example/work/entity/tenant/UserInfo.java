package com.example.work.entity.tenant;


import lombok.Data;
import net.bytebuddy.asm.Advice;

import java.time.LocalDateTime;

@Data
public class UserInfo {
    private Long id;
    // 商户唯一标识
    private String merchant_id;
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
    // 用户角色
    private String role;
    // 是否付费
    private Boolean payStatus;
    // 创建时间
    private LocalDateTime create_time;
    // 更新时间
    private LocalDateTime update_time;
}
