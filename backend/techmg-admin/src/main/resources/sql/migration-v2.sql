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

-- 3. Create tech_reform_task (replaces old tech_governance_task for new features)
CREATE TABLE IF NOT EXISTS `tech_reform_task` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `task_name` VARCHAR(200) NOT NULL COMMENT '任务名称',
    `task_category` VARCHAR(50) DEFAULT '' COMMENT '任务大类(字典)',
    `task_subcategory` VARCHAR(50) DEFAULT '' COMMENT '任务小类(字典)',
    `task_source` VARCHAR(100) DEFAULT '' COMMENT '任务来源(字典+手输)',
    `description` TEXT COMMENT '任务描述/背景/目标',
    `start_date` DATE DEFAULT NULL COMMENT '开始日期',
    `end_date` DATE DEFAULT NULL COMMENT '结束日期',
    `task_owner` VARCHAR(64) DEFAULT '' COMMENT '牵头人',
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/IN_PROGRESS/COMPLETED/CLOSED',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `create_by` VARCHAR(64) DEFAULT '',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `update_by` VARCHAR(64) DEFAULT '',
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    INDEX `idx_task_status` (`status`),
    INDEX `idx_task_category` (`task_category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技改父任务表';

CREATE TABLE IF NOT EXISTS `tech_reform_subtask` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `parent_task_id` BIGINT NOT NULL COMMENT '父任务ID (FK->tech_reform_task.id)',
    `subtask_name` VARCHAR(200) NOT NULL COMMENT '子任务名',
    `db_types` VARCHAR(500) DEFAULT '' COMMENT '数据库类型,JSON数组',
    `data_source` VARCHAR(20) NOT NULL DEFAULT 'NONE' COMMENT '数据来源: FILE_IMPORT/MANUAL/NONE',
    `description` TEXT COMMENT '子任务描述',
    `departments` VARCHAR(1000) DEFAULT '' COMMENT '涉及部门,JSON数组',
    `app_scope` VARCHAR(50) DEFAULT 'ALL' COMMENT '应用范围: ALL/KEY/GOVERNANCE_LIST',
    `affect_negative_asset` TINYINT DEFAULT 0 COMMENT '是否负资产统计',
    `start_date` DATE DEFAULT NULL,
    `end_date` DATE DEFAULT NULL,
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/IN_PROGRESS/COMPLETED/CLOSED',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `create_by` VARCHAR(64) DEFAULT '',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `update_by` VARCHAR(64) DEFAULT '',
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    INDEX `idx_parent_task` (`parent_task_id`),
    INDEX `idx_subtask_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技改子任务表';

CREATE TABLE IF NOT EXISTS `tech_reform_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `subtask_id` BIGINT NOT NULL COMMENT '子任务ID (FK->tech_reform_subtask.id)',
    `application_name` VARCHAR(100) NOT NULL COMMENT '应用名称',
    `governance_item` VARCHAR(200) NOT NULL COMMENT '治理项',
    `issue_description` TEXT COMMENT '问题描述',
    `fix_version` VARCHAR(100) DEFAULT '' COMMENT '修改版本',
    `responsible_person` VARCHAR(64) DEFAULT '' COMMENT '负责人',
    `governance_plan` TEXT COMMENT '治理计划',
    `feedback` TEXT COMMENT '反馈内容',
    `item_status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/IN_PROGRESS/COMPLETED/CLOSED',
    `remark` VARCHAR(500) DEFAULT '' COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `create_by` VARCHAR(64) DEFAULT '',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `update_by` VARCHAR(64) DEFAULT '',
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    INDEX `idx_subtask_id` (`subtask_id`),
    INDEX `idx_app_name` (`application_name`),
    INDEX `idx_item_status` (`item_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='治理清单条目表';
