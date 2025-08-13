package com.example.work.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class UploadDataDTO<T> {
    private T obj; // 泛型：明确主对象类型
    private List<?> list; // 明细列表（如果需要也可指定泛型，如List<U>）
}
