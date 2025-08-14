#!/bin/bash
# 初始化数据库 & 启动服务

echo "等待 MySQL 启动..."
sleep 10

echo "初始化数据库..."
docker exec -i mysql mysql -uroot -p123456 master_db < /workspaces/my_project/mysql-init.sql

echo "启动 Spring Boot..."
cd /workspaces/my_project/backend
mvn spring-boot:run &

echo "启动 Vue..."
cd /workspaces/my_project/frontend
npm install
npm run dev -- --host 0.0.0.0 &
