package com.example.work.entity.master;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("gz_solenoid_valve")
public class GzSolenoidValve {
    // 主键ID
    private Integer id;
    // 名称
    private String name;
    // 点位
    private Integer point;
}
