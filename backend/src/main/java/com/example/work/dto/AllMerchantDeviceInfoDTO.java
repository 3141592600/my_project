package com.example.work.dto;

import lombok.Data;

import java.util.List;

@Data
public class AllMerchantDeviceInfoDTO {

    private String merchantName;
    private List<AllDeviceDTO> devices;  // 该公司所有设备表的汇总数据
}
