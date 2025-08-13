package com.example.work.entity.tenant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceInfo {
    private Long id;                       // 主键
    private String merchant_id;            // 商户唯一标识
    private String device_code;            // 设备编号
    private String device_type;            // 设备类型
    private String latest_status;          // 当前状态（空闲、使用中、故障等）
    private BigDecimal total_output_quantity;  // 出库总量
    private BigDecimal total_input_quantity;   // 入库总量
    private BigDecimal remaining_quantity;     // 剩余数量
    private Boolean pay_status;            // 设备是否缴费使用
    private Boolean is_disabled;           // 设备是否被禁用
    private LocalDateTime create_time;     // 记录创建时间
    private LocalDateTime update_time;     // 记录更新时间
}
