#!/bin/bash
# 后台启动 techmg 前端（Vue 3 + Vite），日志输出到 logs/frontend.log
set -euo pipefail

SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
source "$SCRIPT_DIR/lib/common.sh"

check_env

LOG_DIR="$PROJECT_ROOT/logs"
PID_DIR="$SCRIPT_DIR/.pids"
mkdir -p "$LOG_DIR" "$PID_DIR"

echo "[start-frontend] 后台启动前端开发服务器：http://localhost:5173"
echo "[start-frontend] 日志：$LOG_DIR/frontend.log"

cd "$PROJECT_ROOT/vue-front"
nohup setsid sh -c "npm install && npm run dev" > "$LOG_DIR/frontend.log" 2>&1 &
echo $! > "$PID_DIR/frontend.pid"

echo "[start-frontend] 启动中，约 5-10 秒后可用 ./scripts/check-status.sh 查看状态"
