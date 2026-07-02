-- v1.0 业务表 DDL (10张: 技术治理/数据库治理/巡检/资产/字典)
-- 来源: backend/techmg-admin/src/main/resources/sql/init.sql

CREATE TABLE IF NOT EXISTS `sys_dict_type` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `dict_name` VARCHAR(100) NOT NULL COMMENT '字典名称',
    `dict_type` VARCHAR(100) NOT NULL COMMENT '字典类型',
    `status` TINYINT DEFAULT 1,
    `remark` VARCHAR(500) DEFAULT '',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `create_by` VARCHAR(64) DEFAULT '',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `update_by` VARCHAR(64) DEFAULT '',
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_dict_type` (`dict_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典类型表';

CREATE TABLE IF NOT EXISTS `sys_dict_data` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `dict_type` VARCHAR(100) NOT NULL COMMENT '字典类型',
    `dict_label` VARCHAR(100) NOT NULL COMMENT '字典标签',
    `dict_value` VARCHAR(100) NOT NULL COMMENT '字典值',
    `sort` INT DEFAULT 0,
    `status` TINYINT DEFAULT 1,
    `remark` VARCHAR(500) DEFAULT '',
    `css_class` VARCHAR(100) DEFAULT '' COMMENT '样式属性',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `create_by` VARCHAR(64) DEFAULT '',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `update_by` VARCHAR(64) DEFAULT '',
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    INDEX `idx_dict_type` (`dict_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典数据表';

CREATE TABLE IF NOT EXISTS `tech_governance_task` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `task_name` VARCHAR(200) NOT NULL COMMENT '任务名称',
    `task_year` INT NOT NULL COMMENT '任务年度',
    `task_status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态: PENDING/IN_PROGRESS/COMPLETED',
    `start_date` DATE DEFAULT NULL COMMENT '开始日期',
    `end_date` DATE DEFAULT NULL COMMENT '截止日期',
    `description` TEXT COMMENT '任务描述',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `create_by` VARCHAR(64) DEFAULT '',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `update_by` VARCHAR(64) DEFAULT '',
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    INDEX `idx_task_year` (`task_year`),
    INDEX `idx_task_status` (`task_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技术治理任务主表';

CREATE TABLE IF NOT EXISTS `tech_governance_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `task_id` BIGINT NOT NULL COMMENT '任务ID',
    `application_name` VARCHAR(100) NOT NULL COMMENT '应用名称',
    `governance_item` VARCHAR(200) NOT NULL COMMENT '治理项',
    `issue_description` TEXT COMMENT '问题描述',
    `fix_version` VARCHAR(100) DEFAULT '' COMMENT '修改版本',
    `responsible_person` VARCHAR(64) DEFAULT '' COMMENT '负责人',
    `item_status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态: PENDING/IN_PROGRESS/COMPLETED/CLOSED',
    `remark` VARCHAR(500) DEFAULT '' COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `create_by` VARCHAR(64) DEFAULT '',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `update_by` VARCHAR(64) DEFAULT '',
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    INDEX `idx_task_id` (`task_id`),
    INDEX `idx_app_name` (`application_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技术治理明细';

CREATE TABLE IF NOT EXISTS `db_governance_task` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `task_name` VARCHAR(200) NOT NULL COMMENT '任务名称',
    `db_type` VARCHAR(20) NOT NULL COMMENT '数据库类型: MySQL/PolarDB/GaussDB',
    `task_status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态',
    `start_date` DATE DEFAULT NULL,
    `end_date` DATE DEFAULT NULL,
    `description` TEXT COMMENT '任务描述',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `create_by` VARCHAR(64) DEFAULT '',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `update_by` VARCHAR(64) DEFAULT '',
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    INDEX `idx_db_type` (`db_type`),
    INDEX `idx_db_task_status` (`task_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据库治理任务主表';

CREATE TABLE IF NOT EXISTS `db_governance_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `task_id` BIGINT NOT NULL COMMENT '任务ID',
    `db_instance` VARCHAR(100) NOT NULL COMMENT '数据库实例',
    `governance_item` VARCHAR(200) NOT NULL COMMENT '治理项',
    `issue_description` TEXT COMMENT '问题描述',
    `fix_suggestion` TEXT COMMENT '修复建议',
    `responsible_person` VARCHAR(64) DEFAULT '' COMMENT '负责人',
    `item_status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态',
    `remark` VARCHAR(500) DEFAULT '',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `create_by` VARCHAR(64) DEFAULT '',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `update_by` VARCHAR(64) DEFAULT '',
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    INDEX `idx_db_task_id` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据库治理明细';

CREATE TABLE IF NOT EXISTS `db_inspection_task` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `task_name` VARCHAR(200) NOT NULL COMMENT '巡检名称',
    `inspection_type` VARCHAR(20) NOT NULL DEFAULT 'ROUTINE' COMMENT '类型: ROUTINE/EMERGENCY',
    `task_status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态',
    `inspection_date` DATE DEFAULT NULL COMMENT '巡检日期',
    `db_instance` VARCHAR(100) NOT NULL COMMENT '数据库实例',
    `description` TEXT COMMENT '巡检描述',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `create_by` VARCHAR(64) DEFAULT '',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `update_by` VARCHAR(64) DEFAULT '',
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    INDEX `idx_inspection_type` (`inspection_type`),
    INDEX `idx_inspection_status` (`task_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据库巡检任务主表';

CREATE TABLE IF NOT EXISTS `db_inspection_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `task_id` BIGINT NOT NULL COMMENT '巡检任务ID',
    `check_item` VARCHAR(200) NOT NULL COMMENT '检查项',
    `check_result` VARCHAR(20) NOT NULL DEFAULT 'PASS' COMMENT '结果: PASS/FAIL/WARN',
    `issue_detail` TEXT COMMENT '问题详情',
    `suggestion` TEXT COMMENT '优化建议',
    `responsible_person` VARCHAR(64) DEFAULT '' COMMENT '负责人',
    `item_status` VARCHAR(20) NOT NULL DEFAULT 'OPEN' COMMENT '状态: OPEN/CLOSED',
    `remark` VARCHAR(500) DEFAULT '',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `create_by` VARCHAR(64) DEFAULT '',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `update_by` VARCHAR(64) DEFAULT '',
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    INDEX `idx_inspection_task_id` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='巡检问题明细';

CREATE TABLE IF NOT EXISTS `asset_application` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `app_name` VARCHAR(100) NOT NULL COMMENT '应用名称',
    `app_code` VARCHAR(50) NOT NULL COMMENT '应用编码',
    `dept_id` BIGINT DEFAULT NULL COMMENT '所属部门ID',
    `app_type` VARCHAR(20) DEFAULT '' COMMENT '应用类型',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0停用 1运行',
    `description` VARCHAR(500) DEFAULT '' COMMENT '描述',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `create_by` VARCHAR(64) DEFAULT '',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `update_by` VARCHAR(64) DEFAULT '',
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_app_code` (`app_code`),
    INDEX `idx_dept_id` (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应用资产主表';

CREATE TABLE IF NOT EXISTS `asset_param` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `app_id` BIGINT NOT NULL COMMENT '应用ID',
    `param_name` VARCHAR(100) NOT NULL COMMENT '参数名称',
    `param_value` TEXT COMMENT '参数值',
    `param_type` VARCHAR(20) DEFAULT '' COMMENT '参数类型',
    `env` VARCHAR(20) DEFAULT 'PROD' COMMENT '环境: DEV/TEST/PROD',
    `description` VARCHAR(500) DEFAULT '' COMMENT '参数说明',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `create_by` VARCHAR(64) DEFAULT '',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `update_by` VARCHAR(64) DEFAULT '',
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    INDEX `idx_app_id` (`app_id`),
    INDEX `idx_param_name` (`param_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应用参数表';

CREATE TABLE IF NOT EXISTS `asset_code` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `app_id` BIGINT NOT NULL COMMENT '应用ID',
    `code_type` VARCHAR(20) NOT NULL COMMENT '代码分类: SELF_DEVELOPED/THIRD_PARTY/OPEN_SOURCE',
    `repo_name` VARCHAR(200) NOT NULL COMMENT '仓库名称',
    `repo_url` VARCHAR(500) DEFAULT '' COMMENT '仓库地址',
    `language` VARCHAR(20) DEFAULT '' COMMENT '编程语言',
    `line_count` BIGINT DEFAULT 0 COMMENT '代码行数',
    `description` VARCHAR(500) DEFAULT '' COMMENT '描述',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `create_by` VARCHAR(64) DEFAULT '',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `update_by` VARCHAR(64) DEFAULT '',
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    INDEX `idx_app_id` (`app_id`),
    INDEX `idx_code_type` (`code_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='代码资产表';

CREATE TABLE IF NOT EXISTS `asset_procedure` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `app_id` BIGINT NOT NULL COMMENT '应用ID',
    `db_instance` VARCHAR(100) NOT NULL COMMENT '数据库实例',
    `procedure_name` VARCHAR(200) NOT NULL COMMENT '存储过程名称',
    `procedure_type` VARCHAR(20) DEFAULT 'PROCEDURE' COMMENT '类型: PROCEDURE/FUNCTION/PACKAGE',
    `line_count` INT DEFAULT 0 COMMENT '行数',
    `description` VARCHAR(500) DEFAULT '' COMMENT '描述',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `create_by` VARCHAR(64) DEFAULT '',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `update_by` VARCHAR(64) DEFAULT '',
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    INDEX `idx_app_id` (`app_id`),
    INDEX `idx_db_instance` (`db_instance`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='存储过程量表';
