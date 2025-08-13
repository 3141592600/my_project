package com.example.work.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一响应结果封装类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    // 成功返回（带数据）
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    // 成功返回（不带数据）
    public static Result<Void> success() {
        return new Result<>(200, "操作成功", null);
    }

    // 失败返回（自定义状态码和信息）
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    // 通用失败
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }
}
