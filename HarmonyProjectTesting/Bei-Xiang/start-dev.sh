#!/bin/bash

echo "========================================"
echo "启动后端服务（使用远程数据库）"
echo "========================================"
echo ""
echo "正在启动，请稍候..."
echo ""

mvn spring-boot:run -Dspring-boot.run.profiles=dev

