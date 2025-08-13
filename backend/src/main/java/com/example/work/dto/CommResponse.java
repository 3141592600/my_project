package com.example.work.dto;

import lombok.Data;

/**
 * 通用响应基类
 * 定义所有响应类的公共字段，便于统一接口响应格式
 */
@Data
public class CommResponse {
    /**
     * 响应状态码
     * 200：表示操作成功
     * 非200：表示操作失败（具体错误码可自定义扩展）
     */
    private int Code = 200; // 默认值为200（成功）
}
