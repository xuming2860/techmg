#!/bin/bash
# 检查 techmg 前后端运行状态，并显示最近日志路径
set -euo pipefail

SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
source "$SCRIPT_DIR/lib/common.sh"

echo "[check-status] 检查服务状态..."

if curl -sf http://localhost:8080/api/health >/dev/null 2>&1; then
  echo "  后端：OK  http://localhost:8080/api/health"
else
  echo "  后端：DOWN http://localhost:8080"
fi

if curl -sf http://localhost:5173 >/dev/null 2>&1; then
  echo "  前端：OK  http://localhost:5173"
else
  echo "  前端：DOWN http://localhost:5173"
fi

echo "[check-status] 日志路径：$PROJECT_ROOT/logs/"
