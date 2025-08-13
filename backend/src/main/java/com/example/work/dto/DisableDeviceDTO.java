package com.example.work.dto;

import lombok.Data;

@Data
public class DisableDeviceDTO {

    private String merchantName;
    private String deviceName;
    private Boolean isDisabled;
}
