# techmg 开发脚本

脚本均从项目根目录 `./scripts/` 运行，自动推导项目根路径。

## 环境要求

- JDK 17+（当前环境为 JDK 21，兼容）
- Maven 3.9+
- Node.js 18+ / npm 9+

## 脚本清单

| 脚本 | 说明 |
|------|------|
| `start-backend.sh` | 后台启动后端 `http://localhost:8080`，日志 `logs/backend.log` |
| `start-frontend.sh` | 后台安装依赖并启动前端 `http://localhost:5173`，日志 `logs/frontend.log` |
| `start-all.sh` | 后台启动后端+前端 |
| `stop-all.sh` | 结束占用 `8080` / `5173` 端口的进程 |
| `check-status.sh` | 检查前后端是否可访问，并提示日志路径 |
| `build-backend.sh` | 全量构建后端模块，`-DskipTests` |
| `build-frontend.sh` | 安装依赖并构建前端测试包 |

## 常用命令

```bash
# 一键后台启动
./scripts/start-all.sh

# 查看状态
./scripts/check-status.sh

# 关闭所有开发服务
./scripts/stop-all.sh

# 开发日志
 tail -f logs/backend.log
 tail -f logs/frontend.log
```

> 脚本使用 `setsid + nohup` 将服务作为独立进程后台运行，退出终端后服务仍保留。
