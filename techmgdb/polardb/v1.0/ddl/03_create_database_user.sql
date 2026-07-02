-- v1.0 数据库和用户初始化
-- 来源: backend/techmg-admin/src/main/resources/sql/archive/2026-06-30-setup.sql
-- 执行: mysql -uroot -p < 03_create_database_user.sql

CREATE USER IF NOT EXISTS 'techmg'@'%' IDENTIFIED BY 'Tech@123++**';
CREATE DATABASE IF NOT EXISTS tmvp DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
GRANT ALL PRIVILEGES ON tmvp.* TO 'techmg'@'%';
FLUSH PRIVILEGES;
