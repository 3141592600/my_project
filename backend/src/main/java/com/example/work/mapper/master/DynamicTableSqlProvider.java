package com.example.work.mapper.master;

import java.util.Map;
import java.util.stream.Collectors;

public class DynamicTableSqlProvider {
    @SuppressWarnings("unchecked")
    public String insertData(Map<String, Object> params) {
        String tableName = (String) params.get("tableName");
        Map<String, Object> data = (Map<String, Object>) params.get("data");

        String columns = data.keySet().stream()
                .map(col -> "`" + col + "`")
                .collect(Collectors.joining(", "));

        String values = data.keySet().stream()
                .map(col -> "#{" + "data." + col + "}")
                .collect(Collectors.joining(", "));

        return "INSERT INTO `" + tableName + "` (" + columns + ") VALUES (" + values + ")";
    }
}
