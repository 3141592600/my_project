package com.example.work.entity.master;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("gz_raw_material_storage")
public class GzRawMaterialStorage {
    // 主键ID
    private Integer id;
    // 批次号
    private String batch_number;
    // 入库数量
    private BigDecimal quantity;
    // 产地
    private String origin_place;
    // 入库时间
    private LocalDateTime create_time;
    // 颜色
    private String color;
    // 供应商uuid
    private String supplier_uuid;
    // 唯一编号
    private String uuid;
    // 可用数量
    private BigDecimal available_quantity;
}
