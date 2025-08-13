package com.example.work.entity.master;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("gz_formula_record")
public class GzFormulaRecord {
    // 主键ID
    private Integer id;
    // 颜色
    private String color;
    // 占比
    private BigDecimal ratio;
    // 唯一编码
    private String uuid;
    // 配方唯一编码
    private String formula_uuid;
    // 重量
    private BigDecimal weight;
    // 创建时间
    private LocalDateTime create_time;
    // 更新时间
    private LocalDateTime update_time;
}
