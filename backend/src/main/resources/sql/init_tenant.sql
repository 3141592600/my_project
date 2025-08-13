-- 创建数据库
CREATE DATABASE IF NOT EXISTS tenant_xxx DEFAULT CHARACTER SET utf8mb4;

-- 切换数据库后创建表
USE tenant_xxx;

CREATE TABLE user_info (
                           id VARCHAR(32) PRIMARY KEY,
                           username VARCHAR(50) NOT NULL,
                           email VARCHAR(100),
                           created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE device_info (
                             id VARCHAR(32) PRIMARY KEY,
                             device_name VARCHAR(50) NOT NULL,
                             type VARCHAR(20),
                             created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);