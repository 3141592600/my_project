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
# 如果需要，可以在这里额外导入本地 SQL（除了 mysql-init.sql）
# docker exec -i mysql mysql -uroot -p123456 master_db < /workspace/your-extra.sql

echo "==== [3/4] 安装 Vue 依赖 ===="
cd /workspace/frontend
npm install

echo "==== [4/4] 启动 Spring Boot 后端 ===="
cd /workspace/backend
mvn clean install -DskipTests

echo "==== 初始化完成！===="
echo "你可以用以下命令手动启动项目："
echo "后端: docker exec -it springboot mvn spring-boot:run"
echo "前端: docker exec -it vue npm run dev -- --host 0.0.0.0"
