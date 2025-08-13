package com.example.work.dto;

import com.example.work.entity.tenant.DeviceInfo;
import lombok.Data;


@Data
public class AllDeviceDTO {
    private String deviceName;  // 哪张表的数据
    private DeviceInfo data;  // 该表最大id的那条数据字段值
}
