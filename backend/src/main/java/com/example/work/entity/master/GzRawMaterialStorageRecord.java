package com.example.work.entity.master;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("gz_raw_material_storage_record")
public class GzRawMaterialStorageRecord {
    // 主键ID
    private Integer id;
    // 批次号
    private String batch_number;
    // 数量
    private BigDecimal quantity;
    // 产地
    private String origin_place;
    // 时间
    private LocalDateTime create_time;
    // 颜色
    private String color;
    // 供应商uuid
    private String supplier_uuid;
    // 入库记录编号
    private String raw_material_storage_uuid;
    // 唯一编号
    private String uuid;
    // 0 = 入库 1 = 正常放料 2 = 废料
    private Integer i_type;
    // 操作者
    private String user;
}
