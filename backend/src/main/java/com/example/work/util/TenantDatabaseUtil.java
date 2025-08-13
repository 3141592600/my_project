package com.example.work.util;
//自动建库 + 建表

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TenantDatabaseUtil {

    private final JdbcTemplate jdbcTemplate;

    public TenantDatabaseUtil(@Qualifier("masterJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 创建新的租户数据库
     */
    public void createDatabase(String dbName) {
        String sql = String.format("CREATE DATABASE IF NOT EXISTS `%s` " +
                "DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci", dbName);
        jdbcTemplate.execute(sql);
        log.info("✅ 租户数据库创建成功：{}", dbName);
    }

    /**
     * 初始化租户库内的 user_info 与 device_info 表
     */
    public void createTablesInTenantDb(String dbName) {
        String userInfoSql = "CREATE TABLE IF NOT EXISTS `" + dbName + "`.user_info (" +
                "id BIGINT PRIMARY KEY AUTO_INCREMENT," +
                "merchant_id VARCHAR(64)," +
                "username VARCHAR(64)," +
                "password VARCHAR(128)," +
                "real_name VARCHAR(64)," +
                "phone VARCHAR(32)," +
                "email VARCHAR(64)," +
                "role VARCHAR(32)," +
                "pay_status BOOLEAN DEFAULT FALSE," +
                "create_time DATETIME," +
                "update_time DATETIME" +
                ")";

        String createGzFormulaSql = "CREATE TABLE IF NOT EXISTS `" + dbName + "`.gz_formula (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(255) NOT NULL, " +
                "code VARCHAR(100), " +
                "customer_name VARCHAR(255), " +
                "color_code VARCHAR(100), " +
                "remark TEXT, " +
                "uuid VARCHAR(100) UNIQUE, " +
                "create_time DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;";

        String createGzFormulaRecordSql = "CREATE TABLE IF NOT EXISTS `" + dbName + "`.gz_formula_record (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "color VARCHAR(100), " +
                "ratio DECIMAL(10,4), " +
                "uuid VARCHAR(100) UNIQUE, " +
                "formula_uuid VARCHAR(100), " +
                "weight DECIMAL(18,4), " +
                "create_time DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
                "INDEX idx_formula_uuid (formula_uuid)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;";

        String createGzSupplierSql = "CREATE TABLE IF NOT EXISTS `" + dbName + "`.gz_supplier (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(255) NOT NULL, " +
                "create_time DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
                "uuid VARCHAR(100) UNIQUE" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;";

        String createGzProductionRecordsDetailSql = "CREATE TABLE IF NOT EXISTS `" + dbName + "`.gz_production_records_detail (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "batch_number VARCHAR(100) NOT NULL, " +
                "quantity DECIMAL(18,3) DEFAULT 0.000, " +
                "origin_place VARCHAR(255), " +
                "create_time DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "color VARCHAR(64), " +
                "uuid VARCHAR(100) UNIQUE, " +
                "supplier_uuid VARCHAR(100), " +
                "formula_uuid VARCHAR(100), " +
                "production_records_uuid VARCHAR(100)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;";

        String createGzProductionRecordsSql = "CREATE TABLE IF NOT EXISTS `" + dbName + "`.gz_production_records (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "`user` VARCHAR(100), " +  // user 是MySQL关键字，用反引号包起来
                "create_time DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "number VARCHAR(100), " +
                "uuid VARCHAR(100) UNIQUE" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;";

        String createGzRawMaterialStorageRecordSql = "CREATE TABLE IF NOT EXISTS `" + dbName + "`.gz_raw_material_storage_record (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "batch_number VARCHAR(100), " +
                "quantity DECIMAL(18,3), " +
                "origin_place VARCHAR(255), " +
                "create_time DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "color VARCHAR(100), " +
                "supplier_uuid VARCHAR(100), " +
                "raw_material_storage_uuid VARCHAR(100), " +
                "uuid VARCHAR(100) UNIQUE, " +
                "i_type INT COMMENT '0=入库, 1=正常放料, 2=废料'," +
                "`user` VARCHAR(64) COMMENT '操作人'" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;";

        jdbcTemplate.execute(userInfoSql);
        jdbcTemplate.execute(createGzFormulaSql);
        jdbcTemplate.execute(createGzFormulaRecordSql);
        jdbcTemplate.execute(createGzSupplierSql);
        jdbcTemplate.execute(createGzProductionRecordsDetailSql);
        jdbcTemplate.execute(createGzProductionRecordsSql);
        jdbcTemplate.execute(createGzRawMaterialStorageRecordSql);

        log.info("✅ 租户库 创建成功：{}", dbName);
    }
}
