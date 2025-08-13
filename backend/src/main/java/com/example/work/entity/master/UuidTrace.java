package com.example.work.entity.master;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("uuid_trace")
public class UuidTrace {
    // 主键ID，自增
    private Long id;

    // 商户ID
    private String merchantId;

    // 表名称
    private String tableName;

    // 最新UUID值
    private String latestUuid;

    // 更新时间，自动更新
    private LocalDateTime updatedTime;
}
