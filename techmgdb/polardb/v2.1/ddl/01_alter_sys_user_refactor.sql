-- v2.1 DDL: 重构 sys_user 表
-- auth_no→user_id(12位), 删password/real_name/email/phone/ad_account/dept_id, username=中文名
-- 执行: mysql -u techmg -p --default-character-set=utf8mb4 tmvp < 01_alter_sys_user_refactor.sql

-- 1. 重命名 auth_no → user_id
ALTER TABLE sys_user CHANGE `auth_no` `user_id` VARCHAR(12) NOT NULL COMMENT '统一认证号(12位数字)';

-- 2. 将 real_name 数据迁移到 username（username 旧值为 auth_no，用 real_name 覆盖）
UPDATE sys_user SET username = real_name WHERE real_name IS NOT NULL AND real_name != '';

-- 3. 删除旧列
ALTER TABLE sys_user DROP COLUMN `password`;
ALTER TABLE sys_user DROP COLUMN `real_name`;
ALTER TABLE sys_user DROP COLUMN `email`;
ALTER TABLE sys_user DROP COLUMN `phone`;
ALTER TABLE sys_user DROP COLUMN `ad_account`;
ALTER TABLE sys_user DROP INDEX `idx_dept_id`;
ALTER TABLE sys_user DROP COLUMN `dept_id`;

-- 4. 重建唯一索引
ALTER TABLE sys_user DROP INDEX `uk_auth_no`;
ALTER TABLE sys_user ADD UNIQUE INDEX `uk_user_id` (`user_id`);

-- 5. 删除 sys_dept 表（前端未使用）
DROP TABLE IF EXISTS `sys_dept`;
