-- v1.0 种子数据: 5 个角色
-- 来源: backend/techmg-admin/src/main/resources/sql/init.sql

INSERT INTO `sys_role` (`role_name`, `role_code`, `description`, `sort`, `status`) VALUES
('平台管理员', 'PLATFORM_ADMIN', '全局管理所有用户/角色/权限/菜单/部门配置', 1, 1),
('部门管理员', 'DEPT_ADMIN', '本部门用户管理、本部门资产管理', 2, 1),
('部门DBA', 'DEPT_DBA', '数据库治理/巡检任务操作、数据库参数管理', 3, 1),
('普通用户', 'NORMAL_USER', '填写反馈、确认治理任务、查看本部门数据', 4, 1),
('游客', 'GUEST', '查看公开报表、治理情况概览（只读）', 5, 1);
