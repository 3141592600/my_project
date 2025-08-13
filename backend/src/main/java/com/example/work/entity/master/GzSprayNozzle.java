package com.example.work.entity.master;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("gz_spray_nozzle")
public class GzSprayNozzle {
    // 主键ID
    private Integer id;
    // 名称
    private String name;
    // 颜色
    private String color;
    // 点位
    private Integer point;
    // 阀门组主键
    private Integer solenoid_valveId;
    // 点喷提前量(克)
    private Integer spray_advance;
    // 点喷提前量稳定时间
    private Integer spray_advance_stabilizationTime;
    // 点喷时间(毫秒)
    private Integer spray_time;
    // 点喷稳定时间(毫秒)
    private Integer spray_stabilizationTime;
    // 标号
    private String label;
}
