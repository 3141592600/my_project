#!/bin/bash
echo "等待 MySQL 启动..."
sleep 10

echo "启动 Spring Boot..."
cd /workspace/backend
mvn spring-boot:run &

echo "启动 Vue..."
cd /workspace/frontend
npm install
npm run dev -- --host 0.0.0.0 &
