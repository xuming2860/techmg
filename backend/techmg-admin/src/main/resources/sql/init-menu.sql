-- 初始化菜单数据和角色菜单关联
-- 执行方式: docker exec -i mysql mysql -uroot -p --default-character-set=utf8mb4 tmvp < init-menu.sql
-- 注意: 必须指定 --default-character-set=utf8mb4，否则中文会乱码
--
-- 菜单 ID 编排:
--   1        首页 (上下布局)
--   10-13    技术管理 (上左右布局)
--   20-24    资产管理 (上左右布局)
--   30-31    生产管理 (上左右布局)
--   40-44    设置 (上左右布局, 仅 PLATFORM_ADMIN 可见)
--
-- layout: top=上下布局, side=上左右布局
-- type: 0=目录, 1=菜单, 2=按钮

-- ============================================================
-- 清空旧数据
-- ============================================================
DELETE FROM sys_role_menu;
DELETE FROM sys_menu;

-- ============================================================
-- 首页 (上下布局)
-- ============================================================
INSERT INTO sys_menu (id, parent_id, menu_name, path, component, icon, type, perms, sort, status, visible) VALUES
(1, 0, '首页', '/dashboard', 'views/dashboard/index', 'HomeFilled', 1, '', 1, 1, 1);

-- ============================================================
-- 技术管理 (上左右布局)
-- ============================================================
INSERT INTO sys_menu (id, parent_id, menu_name, path, component, icon, type, perms, sort, status, visible) VALUES
(10, 0, '技术管理', '', '', 'Setting', 0, '', 2, 1, 1),
(11, 10, '技术治理任务', '/tech-governance', 'views/tech-governance/index', '', 1, '', 1, 1, 1),
(12, 10, '数据库治理任务', '/db-governance', 'views/db-governance/index', '', 1, '', 2, 1, 1),
(13, 10, '数据库巡检任务', '/db-inspection', 'views/db-inspection/index', '', 1, '', 3, 1, 1);

-- ============================================================
-- 资产管理 (上左右布局)
-- ============================================================
INSERT INTO sys_menu (id, parent_id, menu_name, path, component, icon, type, perms, sort, status, visible) VALUES
(20, 0, '资产管理', '', '', 'FolderOpened', 0, '', 3, 1, 1),
(21, 20, '应用资产管理', '/asset/app', 'views/asset/app/index', '', 1, '', 1, 1, 1),
(22, 20, '参数管理', '/asset/param', 'views/asset/param/index', '', 1, '', 2, 1, 1),
(23, 20, '代码资产管理', '/asset/code', 'views/asset/code/index', '', 1, '', 3, 1, 1),
(24, 20, '存储过程管理', '/asset/procedure', 'views/asset/procedure/index', '', 1, '', 4, 1, 1);

-- ============================================================
-- 生产管理 (上左右布局)
-- ============================================================
INSERT INTO sys_menu (id, parent_id, menu_name, path, component, icon, type, perms, sort, status, visible) VALUES
(30, 0, '生产管理', '', '', 'Monitor', 0, '', 4, 1, 1),
(31, 30, '生产管理', '/production', 'views/production/index', '', 1, '', 1, 1, 1);

-- ============================================================
-- 设置 (上左右布局, 仅 PLATFORM_ADMIN 可见)
-- ============================================================
INSERT INTO sys_menu (id, parent_id, menu_name, path, component, icon, type, perms, sort, status, visible) VALUES
(40, 0, '设置', '', '', 'Tools', 0, '', 5, 1, 1),
(41, 40, '用户管理', '/system/user', 'views/system/user/index', '', 1, 'system:user:list', 1, 1, 1),
(42, 40, '角色管理', '/system/role', 'views/system/role/index', '', 1, 'system:role:list', 2, 1, 1),
(43, 40, '菜单管理', '/system/menu', 'views/system/menu/index', '', 1, 'system:menu:list', 3, 1, 1),
(44, 40, '部门管理', '/system/dept', 'views/system/dept/index', '', 1, 'system:dept:list', 4, 1, 1);

-- ============================================================
-- 角色菜单关联
-- role_id: 1=PLATFORM_ADMIN, 2=DEPT_ADMIN, 3=DEPT_DBA, 4=NORMAL_USER, 5=GUEST
-- ============================================================

-- PLATFORM_ADMIN: 全部菜单 (1, 10-44)
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(1,1),
(1,10),(1,11),(1,12),(1,13),
(1,20),(1,21),(1,22),(1,23),(1,24),
(1,30),(1,31),
(1,40),(1,41),(1,42),(1,43),(1,44);

-- DEPT_ADMIN: 首页 + 技术管理 + 资产管理 + 生产管理 (无设置)
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(2,1),
(2,10),(2,11),(2,12),(2,13),
(2,20),(2,21),(2,22),(2,23),(2,24),
(2,30),(2,31);

-- DEPT_DBA: 首页 + DB治理 + DB巡检 + 资产管理
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(3,1),
(3,12),(3,13),
(3,20),(3,21),(3,22),(3,23),(3,24);

-- NORMAL_USER: 首页 + 技术管理(全部)
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(4,1),
(4,10),(4,11),(4,12),(4,13);

-- GUEST: 仅首页
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(5,1);
