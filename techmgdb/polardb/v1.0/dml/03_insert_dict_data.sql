-- v1.0 字典种子数据
-- 来源: backend/techmg-admin/src/main/resources/sql/init.sql

INSERT INTO sys_dict_type (dict_name, dict_type) VALUES ('治理状态', 'governance_status');
INSERT INTO sys_dict_data (dict_type, dict_label, dict_value, sort) VALUES
('governance_status', '待处理', 'PENDING', 1),
('governance_status', '进行中', 'IN_PROGRESS', 2),
('governance_status', '已完成', 'COMPLETED', 3),
('governance_status', '已关闭', 'CLOSED', 4);
