package com.example.work.entity.master;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("gz_supplier")
public class GzSupplier {
    // 主键ID
    private Integer id;
    // 供应商名称
    private String name;
    // 创建时间
    private LocalDateTime create_time;
    // 更新时间
    private LocalDateTime update_time;
    // uuid
    private String uuid;
}
