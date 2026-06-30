-- Add password column to sys_user for BCrypt-encrypted local auth
-- If column already exists, the ALTER TABLE will fail safely (ignored in automation).
ALTER TABLE sys_user ADD COLUMN `password` VARCHAR(200) DEFAULT '' COMMENT '密码(BCrypt加密)' AFTER `auth_no`;
