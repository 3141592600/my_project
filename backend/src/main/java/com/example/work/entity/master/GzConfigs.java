package com.example.work.entity.master;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("gz_configs")
public class GzConfigs {
    // 配置键
    private String uKey;
    // 配置值
    private String uVal;
}
