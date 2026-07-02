-- v2.0 字典种子数据
-- 来源: backend/techmg-admin/src/main/resources/sql/init.sql

INSERT IGNORE INTO sys_dict_type (dict_name, dict_type) VALUES
('任务大类', 'task_category'),
('任务小类', 'task_subcategory'),
('任务来源', 'task_source'),
('子任务数据库类型', 'subtask_db_type');

INSERT IGNORE INTO sys_dict_data (dict_type, dict_label, dict_value, sort) VALUES
('task_category', '数据库治理', 'database_governance', 1),
('task_category', '技术优化改造', 'tech_optimization', 2),
('task_category', '信创转型类', 'xinchuang', 3),
('task_category', '信息安全提升', 'security_improve', 4),
('task_category', '运维转型提升', 'ops_transformation', 5),
('task_subcategory', '应用代码', 'app_code', 1),
('task_subcategory', '开源软件类', 'open_source', 2),
('task_subcategory', '外购软件类', 'purchased_software', 3),
('task_subcategory', '技术栈', 'tech_stack', 4),
('task_subcategory', '运维能力提升', 'ops_capability', 5),
('task_subcategory', '元数据信息', 'metadata', 6),
('task_source', '项目办', 'project_office', 1),
('task_source', '架构管理一部', 'arch_dept_1', 2),
('task_source', '架构管理二部', 'arch_dept_2', 3),
('task_source', '云计算实验室', 'cloud_lab', 4),
('task_source', '上海技术部', 'shanghai_tech', 5),
('subtask_db_type', 'GaussDB', 'gaussdb', 1),
('subtask_db_type', 'PolarDB', 'polardb', 2),
('subtask_db_type', 'MySQL', 'mysql', 3);
