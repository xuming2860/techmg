#!/bin/bash
# 后台启动 techmg 后端（techmg-admin），日志输出到 logs/backend.log
set -euo pipefail

SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
source "$SCRIPT_DIR/lib/common.sh"

check_env

LOG_DIR="$PROJECT_ROOT/logs"
PID_DIR="$SCRIPT_DIR/.pids"
mkdir -p "$LOG_DIR" "$PID_DIR"

echo "[start-backend] 后台启动 Spring Boot 后端：http://localhost:8080"
echo "[start-backend] 日志：$LOG_DIR/backend.log"

cd "$PROJECT_ROOT/backend/techmg-admin"
nohup setsid mvn spring-boot:run > "$LOG_DIR/backend.log" 2>&1 &
echo $! > "$PID_DIR/backend.pid"

echo "[start-backend] 启动中，约 10-20 秒后可用 ./scripts/check-status.sh 查看状态"
