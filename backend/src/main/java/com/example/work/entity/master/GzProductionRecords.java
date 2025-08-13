package com.example.work.entity.master;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("gz_production_records")
public class GzProductionRecords {
    // 主键ID
    private Integer id;
    // 生产人
    private String user;
    // 生产日期
    private LocalDateTime create_time;
    // 生产编号
    private String number;
    // uuid
    private String uuid;
}
