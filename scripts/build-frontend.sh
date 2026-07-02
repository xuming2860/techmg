#!/bin/bash
# 构建前端测试包
set -euo pipefail

SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
source "$SCRIPT_DIR/lib/common.sh"

check_env

echo "[build-frontend] 安装依赖并构建前端测试包..."
cd "$PROJECT_ROOT/vue-front"
npm install
npm run build:test
