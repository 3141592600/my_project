package com.example.work.dto;

import lombok.Data;

/**
 * 最新UUID查询响应类
 * 继承CommResponse，包含是否存在UUID及具体UUID值
 */
@Data
public class UUIDLatest extends CommResponse {
    /**
     * 是否存在UUID
     * true：表示存在有效UUID
     * false：表示表中无数据或未查询到UUID
     */
    private boolean ExistUUID;

    /**
     * UUID对应的字符串
     * 当ExistUUID为true时，该字段为有效的UUID值；否则可为空
     */
    private String UUID;
}
