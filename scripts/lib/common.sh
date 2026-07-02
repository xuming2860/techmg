#!/bin/bash
# techmg 脚本公共变量与环境检查

SCRIPT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)
PROJECT_ROOT=$(cd "$SCRIPT_DIR/.." && pwd)

check_command() {
  local cmd=$1
  if ! command -v "$cmd" >/dev/null 2>&1; then
    echo "[error] 缺少必需命令：$cmd，请先安装。"
    exit 1
  fi
}

check_env() {
  echo "[env] 项目根目录：$PROJECT_ROOT"
  check_command java
  check_command mvn
  check_command node
  check_command npm
  echo "[env] java: $(java -version 2>&1 | head -1)"
  echo "[env] mvn: $(mvn -v 2>&1 | head -1)"
  echo "[env] node: $(node -v)"
  echo "[env] npm: $(npm -v)"
}
