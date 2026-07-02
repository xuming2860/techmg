-- v1.0 业务表 DDL (2张: 字典类型 + 字典数据)
-- 占位表 (技术治理/数据库治理/巡检/资产) 已在 v2.1 移除，待具体业务需求时再创建
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
