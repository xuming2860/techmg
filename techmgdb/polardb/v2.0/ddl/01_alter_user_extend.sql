-- v2.0 DDL: ALTER 扩展 sys_user 表
-- 来源: backend/techmg-admin/src/main/resources/sql/migration-v2.sql + alter-user-password.sql

ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS `ad_account` VARCHAR(64) DEFAULT '' COMMENT 'AD账号' AFTER `dept_id`;
ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS `branch_id` VARCHAR(32) DEFAULT '' COMMENT '机构号' AFTER `ad_account`;
ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS `branch_name` VARCHAR(200) DEFAULT '' COMMENT '机构全名' AFTER `branch_id`;
ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS `notes_id` VARCHAR(64) DEFAULT '' COMMENT 'Notes ID' AFTER `branch_name`;
ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间' AFTER `notes_id`;
ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS `password` VARCHAR(200) DEFAULT '' COMMENT '密码(BCrypt加密)' AFTER `auth_no`;
