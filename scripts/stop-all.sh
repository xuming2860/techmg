#!/bin/bash
# 停止占用 8080 / 5173 端口的开发服务
set -euo pipefail

echo "[stop-all] 停止 techmg 开发服务..."
for port in 8080 5173; do
  pids=$(lsof -ti tcp:"$port" 2>/dev/null || true)
  if [ -n "$pids" ]; then
    echo "[stop-all] 端口 $port 上的进程：$pids"
    kill -TERM $pids 2>/dev/null || true
    sleep 1
    kill -KILL $pids 2>/dev/null || true
  else
    echo "[stop-all] 端口 $port 无运行进程"
  fi
done
echo "[stop-all] 完成"
