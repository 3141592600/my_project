#!/bin/bash
# 等待 MySQL 启动
echo "等待 MySQL 启动..."
sleep 10

# 初始化数据库
echo "初始化数据库..."
docker exec -i mysql mysql -uroot -p123456 master_db < /workspaces/${LOCAL_WORKSPACE_FOLDER}/mysql-init.sql

# 启动 Spring Boot
echo "启动 Spring Boot..."
cd /workspaces/${LOCAL_WORKSPACE_FOLDER}/backend
mvn spring-boot:run &

# 启动 Vue
echo "启动 Vue..."
cd /workspaces/${LOCAL_WORKSPACE_FOLDER}/frontend
npm install
npm run dev -- --host 0.0.0.0 &
