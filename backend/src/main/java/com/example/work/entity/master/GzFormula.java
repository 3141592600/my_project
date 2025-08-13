package com.example.work.entity.master;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("gz_formula")
public class GzFormula {
    // 主键ID
    private Integer id;
    // 配方名称
    private String name;
    // 配方编码
    private String code;
    // 客户名称
    private String customer_name;
    // 色卡号
    private String color_code;
    // 备注
    private String remark;
    // 唯一编码
    private String uuid;
    // 创建时间
    private LocalDateTime create_time;
    // 更新时间
    private LocalDateTime update_time;
}
