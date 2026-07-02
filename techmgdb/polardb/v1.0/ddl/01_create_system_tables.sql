-- v1.0 系统表 DDL (7张: dept/user/role/user_role/menu/role_menu/operation_log)
-- 来源: backend/techmg-admin/src/main/resources/sql/init.sql
-- 执行: mysql -u techmg -p --default-character-set=utf8mb4 tmvp < 01_create_system_tables.sql

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

CREATE TABLE IF NOT EXISTS `sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `auth_no` VARCHAR(50) NOT NULL COMMENT '统一认证号',
    `password` VARCHAR(255) DEFAULT '' COMMENT '密码',
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

CREATE TABLE IF NOT EXISTS `sys_user_role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

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

CREATE TABLE IF NOT EXISTS `sys_role_menu` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `menu_id` BIGINT NOT NULL COMMENT '菜单ID',
    PRIMARY KEY (`id`),
    INDEX `idx_role_id` (`role_id`),
    INDEX `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

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
