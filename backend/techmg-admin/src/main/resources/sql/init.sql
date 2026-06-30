-- 初始化 tmvp 数据库表结构 (17张表 + 5角色DML)
-- 执行方式: docker exec -i mysql mysql -uroot -p --default-character-set=utf8mb4 tmvp < init.sql
-- 注意: 必须指定 --default-character-set=utf8mb4，否则中文会乱码

-- 部门表
CREATE TABLE IF NOT EXISTS `sys_dept` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `dept_name` VARCHAR(100) NOT NULL COMMENT '部门名称',
    `dept_code` VARCHAR(50) NOT NULL COMMENT '部门编码',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父部门ID',
    `ancestors` VARCHAR(500) DEFAULT '' COMMENT '祖级列表',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT DEFAULT 1 COMMENT '状态 0禁用 1启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `create_by` VARCHAR(64) DEFAULT '',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `update_by` VARCHAR(64) DEFAULT '',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除 0正常 1删除',
    PRIMARY KEY (`id`),
    INDEX `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- 用户表
CREATE TABLE IF NOT EXISTS `sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `auth_no` VARCHAR(50) NOT NULL COMMENT '统一认证号',
    `username` VARCHAR(64) NOT NULL COMMENT '用户名',
    `real_name` VARCHAR(64) DEFAULT '' COMMENT '真实姓名',
    `email` VARCHAR(128) DEFAULT '' COMMENT '邮箱',
    `phone` VARCHAR(20) DEFAULT '' COMMENT '手机号',
    `dept_id` BIGINT DEFAULT NULL COMMENT '部门ID',
    `status` TINYINT DEFAULT 1 COMMENT '状态 0禁用 1启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `create_by` VARCHAR(64) DEFAULT '',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `update_by` VARCHAR(64) DEFAULT '',
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_auth_no` (`auth_no`),
    INDEX `idx_dept_id` (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 角色表
CREATE TABLE IF NOT EXISTS `sys_role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `role_name` VARCHAR(64) NOT NULL COMMENT '角色名称',
    `role_code` VARCHAR(50) NOT NULL COMMENT '角色编码',
    `description` VARCHAR(200) DEFAULT '' COMMENT '描述',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT DEFAULT 1 COMMENT '状态',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `create_by` VARCHAR(64) DEFAULT '',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `update_by` VARCHAR(64) DEFAULT '',
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS `sys_user_role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 菜单表
CREATE TABLE IF NOT EXISTS `sys_menu` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `parent_id` BIGINT DEFAULT 0 COMMENT '父菜单ID',
    `menu_name` VARCHAR(50) NOT NULL COMMENT '菜单名称',
    `path` VARCHAR(200) DEFAULT '' COMMENT '路由地址',
    `component` VARCHAR(255) DEFAULT '' COMMENT '组件路径',
    `icon` VARCHAR(100) DEFAULT '' COMMENT '图标',
    `type` TINYINT DEFAULT 1 COMMENT '类型 0目录 1菜单 2按钮',
    `perms` VARCHAR(100) DEFAULT '' COMMENT '权限标识',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT DEFAULT 1 COMMENT '状态',
    `visible` TINYINT DEFAULT 1 COMMENT '是否可见 0隐藏 1可见',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `create_by` VARCHAR(64) DEFAULT '',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `update_by` VARCHAR(64) DEFAULT '',
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    INDEX `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- 角色菜单关联表
CREATE TABLE IF NOT EXISTS `sys_role_menu` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `menu_id` BIGINT NOT NULL COMMENT '菜单ID',
    PRIMARY KEY (`id`),
    INDEX `idx_role_id` (`role_id`),
    INDEX `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- 操作日志表
CREATE TABLE IF NOT EXISTS `sys_operation_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `module` VARCHAR(100) DEFAULT '' COMMENT '模块',
    `operation` VARCHAR(100) DEFAULT '' COMMENT '操作类型',
    `operator` VARCHAR(64) DEFAULT '' COMMENT '操作人',
    `request_param` TEXT COMMENT '请求参数',
    `response_result` TEXT COMMENT '响应结果',
    `ip` VARCHAR(50) DEFAULT '' COMMENT 'IP地址',
    `cost_time` BIGINT DEFAULT 0 COMMENT '耗时(ms)',
    `status` TINYINT DEFAULT 1 COMMENT '状态 0失败 1成功',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 初始角色数据
INSERT INTO `sys_role` (`role_name`, `role_code`, `description`, `sort`, `status`) VALUES
('平台管理员', 'PLATFORM_ADMIN', '全局管理所有用户/角色/权限/菜单/部门配置', 1, 1),
('部门管理员', 'DEPT_ADMIN', '本部门用户管理、本部门资产管理', 2, 1),
('部门DBA', 'DEPT_DBA', '数据库治理/巡检任务操作、数据库参数管理', 3, 1),
('普通用户', 'NORMAL_USER', '填写反馈、确认治理任务、查看本部门数据', 4, 1),
('游客', 'GUEST', '查看公开报表、治理情况概览（只读）', 5, 1);

-- ============================================================
-- 业务表
-- ============================================================

-- 技术治理任务主表
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

-- 技术治理明细
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

-- 数据库治理任务主表
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

-- 数据库治理明细
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

-- 数据库巡检任务主表
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

-- 巡检问题明细
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

-- 应用资产主表
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

-- 应用参数表
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

-- 代码资产表
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

-- 存储过程量表
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

-- ============================================================
-- 字典表
-- ============================================================

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

-- 字典初始数据
INSERT INTO sys_dict_type (dict_name, dict_type) VALUES ('治理状态', 'governance_status');
INSERT INTO sys_dict_data (dict_type, dict_label, dict_value, sort) VALUES
('governance_status', '待处理', 'PENDING', 1),
('governance_status', '进行中', 'IN_PROGRESS', 2),
('governance_status', '已完成', 'COMPLETED', 3),
('governance_status', '已关闭', 'CLOSED', 4);
