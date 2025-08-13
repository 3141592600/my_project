package com.example.work.entity.tenant;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserFieldPermission {
    private Long id;               // 主键
    private String username;       // 用户名
    private String fieldName;      // 字段名
    private Boolean isVisible;     // 是否可见
    private LocalDateTime createTime; // 创建时间
}
