-- ============================================================
-- 数据库初始化归档脚本
-- 日期: 2026-06-30
-- 环境: 本地 Docker MySQL (dev)
-- ============================================================
--
-- 前置条件：MySQL 服务端配置 utf8mb4
--   在 MySQL 配置文件 /etc/mysql/conf.d/charset.cnf 中添加:
--     [client]
--     default-character-set=utf8mb4
--     [mysql]
--     default-character-set=utf8mb4
--     [mysqld]
--     character-set-server=utf8mb4
--     collation-server=utf8mb4_general_ci
--     init-connect='SET NAMES utf8mb4'
--
-- 执行方式（必须指定 --default-character-set=utf8mb4）:
--   docker exec -i mysql mysql -uroot -p --default-character-set=utf8mb4 < setup.sql
-- ============================================================

-- 1. 创建数据库用户
CREATE USER IF NOT EXISTS 'techmg'@'%' IDENTIFIED BY 'Tech@123++**';

-- 2. 创建数据库（必须显式指定 utf8mb4）
CREATE DATABASE IF NOT EXISTS tmvp DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- 3. 授权
GRANT ALL PRIVILEGES ON tmvp.* TO 'techmg'@'%';
FLUSH PRIVILEGES;

-- 4. 验证
SELECT '=== Setup Complete ===' AS status;
SHOW GRANTS FOR 'techmg'@'%';
SELECT DEFAULT_CHARACTER_SET_NAME, DEFAULT_COLLATION_NAME
  FROM information_schema.SCHEMATA WHERE SCHEMA_NAME = 'tmvp';
