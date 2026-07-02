#!/bin/bash
# 构建所有后端模块（跳过测试）
set -euo pipefail

SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
source "$SCRIPT_DIR/lib/common.sh"

check_env

echo "[build-backend] 构建后端所有模块..."
cd "$PROJECT_ROOT/backend"
mvn clean install -DskipTests
