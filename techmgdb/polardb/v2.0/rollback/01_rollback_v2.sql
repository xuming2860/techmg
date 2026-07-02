-- v2.0 Rollback: 撤销 v2.0 变更
-- 删除 v2.0 新增表
DROP TABLE IF EXISTS `tech_reform_item`;
DROP TABLE IF EXISTS `tech_reform_subtask`;
DROP TABLE IF EXISTS `tech_reform_task`;
DROP TABLE IF EXISTS `sys_user_branch`;

-- 回退 sys_user 扩展字段
ALTER TABLE sys_user DROP COLUMN IF EXISTS `last_login_time`;
ALTER TABLE sys_user DROP COLUMN IF EXISTS `notes_id`;
ALTER TABLE sys_user DROP COLUMN IF EXISTS `branch_name`;
ALTER TABLE sys_user DROP COLUMN IF EXISTS `branch_id`;
ALTER TABLE sys_user DROP COLUMN IF EXISTS `ad_account`;
ALTER TABLE sys_user DROP COLUMN IF EXISTS `password`;

-- 删除 v2.0 字典数据
DELETE FROM sys_dict_data WHERE dict_type IN ('task_category', 'task_subcategory', 'task_source', 'subtask_db_type');
DELETE FROM sys_dict_type WHERE dict_type IN ('task_category', 'task_subcategory', 'task_source', 'subtask_db_type');
