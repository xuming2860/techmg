-- v1.0 Rollback: 删除系统表 (逆序)
-- sys_dept 已移除 (v2.1)
DROP TABLE IF EXISTS `sys_operation_log`;
DROP TABLE IF EXISTS `sys_role_menu`;
DROP TABLE IF EXISTS `sys_user_role`;
DROP TABLE IF EXISTS `sys_menu`;
DROP TABLE IF EXISTS `sys_role`;
DROP TABLE IF EXISTS `sys_user`;
