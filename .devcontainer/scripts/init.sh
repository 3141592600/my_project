#!/bin/bash
# 等待 MySQL 启动
echo "等待 MySQL 启动..."
sleep 15

# 初始化数据库
echo "初始化数据库..."
docker exec -i mysql mysql -uroot -p123456 master_db < /workspace/mysql-init.sql

# 启动 Spring Boot
echo "启动 Spring Boot..."
cd /workspace/backend
mvn spring-boot:run &

# 启动 Vue
echo "启动 Vue..."
cd /workspace/frontend
npm install
npm run dev -- --host 0.0.0.0 &
