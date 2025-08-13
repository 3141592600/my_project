package com.example.work.config;

import com.example.work.util.DataSourceContextHolder;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 支持运行时新增数据源的动态数据源
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    // 缓存所有已注册数据源（线程安全）
    private final Map<Object, Object> dataSourceMap = new ConcurrentHashMap<>();

    public void addDataSource(String key, DataSource dataSource) {
        dataSourceMap.put(key, dataSource);
        super.setTargetDataSources(dataSourceMap);
        super.afterPropertiesSet(); // 刷新数据源
    }

    public boolean containsDataSource(String key) {
        return dataSourceMap.containsKey(key);
    }
    @Override
    protected Object determineCurrentLookupKey() {

        return DataSourceContextHolder.getDataSource();
    }
}
