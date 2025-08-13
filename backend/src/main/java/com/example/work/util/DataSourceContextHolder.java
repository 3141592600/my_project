package com.example.work.util;

/**
 * 使用 ThreadLocal 保存当前线程数据源key，保证线程安全
 */
public class DataSourceContextHolder {
    private static final ThreadLocal<String> CONTEXT = new ThreadLocal<>();

    public static void setDataSource(String dataSourceKey) {
        CONTEXT.set(dataSourceKey);
    }

    public static String getDataSource() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
