-- migration-v2.sql: v2.0 database changes

-- 1. Extend sys_user table
ALTER TABLE sys_user ADD COLUMN ad_account VARCHAR(64) DEFAULT '' COMMENT 'AD账号' AFTER dept_id;
ALTER TABLE sys_user ADD COLUMN branch_id VARCHAR(32) DEFAULT '' COMMENT '机构号' AFTER ad_account;
ALTER TABLE sys_user ADD COLUMN branch_name VARCHAR(200) DEFAULT '' COMMENT '机构全名' AFTER branch_id;
ALTER TABLE sys_user ADD COLUMN notes_id VARCHAR(64) DEFAULT '' COMMENT 'Notes ID' AFTER branch_name;
ALTER TABLE sys_user ADD COLUMN last_login_time DATETIME DEFAULT NULL COMMENT '最后登录时间' AFTER notes_id;

-- 2. Create sys_user_branch table
CREATE TABLE IF NOT EXISTS `sys_user_branch` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `branch_id` VARCHAR(32) NOT NULL COMMENT '机构号',
    `branch_name` VARCHAR(200) DEFAULT '' COMMENT '机构名',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户多机构关联表';
