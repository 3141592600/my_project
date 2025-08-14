#!/bin/bash
# 等待 MySQL 启动
echo "等待 MySQL 启动..."
sleep 10

echo "初始化数据库..."
docker exec -i mysql mysql -uroot -p123456 master_db < /workspaces/my_project/mysql-init.sql

echo "Spring Boot 和 Vue 容器由 docker-compose command 自动启动"
