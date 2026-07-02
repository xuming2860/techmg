#!/bin/bash
# 一键后台启动后端 + 前端
set -euo pipefail

SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
source "$SCRIPT_DIR/lib/common.sh"

check_env

echo "[start-all] 启动后端..."
"$SCRIPT_DIR/start-backend.sh"

echo "[start-all] 等待后端 http://localhost:8080/api/health 就绪..."
for i in {1..60}; do
  if curl -sf http://localhost:8080/api/health >/dev/null 2>&1; then
    echo "[start-all] 后端已就绪"
    break
  fi
  sleep 1
done

echo "[start-all] 启动前端..."
"$SCRIPT_DIR/start-frontend.sh"

echo "[start-all] 完成。使用 ./scripts/check-status.sh 查看状态"
