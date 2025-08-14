#!/bin/bash
set -e

echo "==== [1/4] 等待 MySQL 启动 ===="
# 等 MySQL 容器启动完成（最多等 60 秒）
until docker exec mysql mysql -uroot -p123456 -e "SELECT 1;" &>/dev/null; do
  echo "等待 MySQL ..."
  sleep 2
done
echo "MySQL 已启动"

echo "==== [2/4] 初始化 MySQL 数据 ===="
# 如果需要额外初始化数据，可在这里执行
# docker exec -i mysql mysql -uroot -p123456 master_db < /workspace/extra-data.sql

echo "==== [3/4] 启动 Spring Boot 后端 ===="
docker exec -d springboot mvn spring-boot:run

echo "==== [4/4] 启动 Vue 前端 ===="
docker exec -d vue sh -c "npm install && npm run dev -- --host 0.0.0.0"

echo "==== 所有服务已启动 ===="
echo "后端 API: http://localhost:8080"
echo "前端 Vue: http://localhost:5173"
