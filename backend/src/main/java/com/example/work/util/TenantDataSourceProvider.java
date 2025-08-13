package com.example.work.util;

import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

/**
 * 动态创建租户数据源
 */
public class TenantDataSourceProvider {
    public static DataSource createTenantDataSource(String dbName) {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:mysql://localhost:3308/" + dbName + "?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai");
        ds.setUsername("root");
        ds.setPassword("123456");
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        return ds;
    }

}
