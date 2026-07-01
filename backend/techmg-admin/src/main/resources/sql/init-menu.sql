-- 初始化菜单数据和角色菜单关联
-- 执行: docker exec -i mysql mysql -uroot -p --default-character-set=utf8mb4 tmvp < init-menu.sql
--
-- 菜单 ID 编排:
--   1               首页 (上下布局)
--   10,11-14,15-19,20-22  技术管理 (技改任务/数据库管理/技术栈管理)
--   25,26-29        资产管理
--   35,36           生产管理
--   40,41-44        设置 (仅PLATFORM_ADMIN)
--
-- type: 0=目录, 1=菜单; layout: top=上下, side=上左右

DELETE FROM sys_role_menu;
DELETE FROM sys_menu;

-- ====== 首页 ======
INSERT INTO sys_menu (id, parent_id, menu_name, path, component, icon, type, perms, sort, status, visible) VALUES
(1, 0, '首页', '/dashboard', 'views/dashboard/index', 'HomeFilled', 1, '', 1, 1, 1);

-- ====== 技术管理 ======
INSERT INTO sys_menu (id, parent_id, menu_name, path, component, icon, type, perms, sort, status, visible) VALUES
(10, 0, '技术管理', '', '', 'Setting', 0, '', 2, 1, 1),

-- 技改任务管理
(11, 10, '技改任务管理', '', '', 'List', 0, '', 1, 1, 1),
(12, 11, '任务总览', '/tech-reform/overview', 'views/tech-reform/overview/index', '', 1, '', 1, 1, 1),
(13, 11, '子任务管理', '/tech-reform/subtask', 'views/tech-reform/subtask/index', '', 1, '', 2, 1, 1),
-- (14) 技改任务统计 — v2.0已移除，功能并入任务总览和子任务管理

-- 数据库管理
(15, 10, '数据库管理', '', '', 'Coin', 0, '', 2, 1, 1),
(16, 15, 'DBA人员管理', '/db-manage/dba', 'views/db-manage/dba/index', '', 1, '', 1, 1, 1),
(17, 15, '应用数据库管理', '/db-manage/app-db', 'views/db-manage/app-db/index', '', 1, '', 2, 1, 1),
(18, 15, '巡检任务管理', '/db-manage/inspection', 'views/db-manage/inspection/index', '', 1, '', 3, 1, 1),
(19, 15, '慢SQL和长事务管理', '/db-manage/slow-sql', 'views/db-manage/slow-sql/index', '', 1, '', 4, 1, 1),

-- 技术栈管理
(20, 10, '技术栈管理', '', '', 'TrendCharts', 0, '', 3, 1, 1),
(21, 20, '整体视图', '/tech-stack/overview', 'views/tech-stack/overview/index', '', 1, '', 1, 1, 1),
(22, 20, '问题明细', '/tech-stack/issues', 'views/tech-stack/issues/index', '', 1, '', 2, 1, 1);

-- ====== 资产管理 ======
INSERT INTO sys_menu (id, parent_id, menu_name, path, component, icon, type, perms, sort, status, visible) VALUES
(25, 0, '资产管理', '', '', 'FolderOpened', 0, '', 3, 1, 1),
(26, 25, '应用资产健康度', '/asset/health', 'views/asset/health/index', '', 1, '', 1, 1, 1),
(27, 25, '应用信息管理', '', '', 'Files', 0, '', 2, 1, 1),
(28, 27, '重保应用信息管理', '/asset/app-important', 'views/asset/app-important/index', '', 1, '', 1, 1, 1),
(29, 27, '应用重要场景信息管理', '/asset/app-scenario', 'views/asset/app-scenario/index', '', 1, '', 2, 1, 1),
(30, 25, '基地研发范式管理', '/asset/dev-paradigm', 'views/asset/dev-paradigm/index', '', 1, '', 3, 1, 1);

-- ====== 生产管理 ======
INSERT INTO sys_menu (id, parent_id, menu_name, path, component, icon, type, perms, sort, status, visible) VALUES
(35, 0, '生产管理', '', '', 'Monitor', 0, '', 4, 1, 1),
(36, 35, '生产管理', '/production', 'views/production/index', '', 1, '', 1, 1, 1);

-- ====== 数智生态 ======
INSERT INTO sys_menu (id, parent_id, menu_name, path, component, icon, type, perms, sort, status, visible) VALUES
(45, 0, '数智生态', '', '', 'DataAnalysis', 0, '', 5, 1, 1),
(46, 45, '一级领域', '/digital-eco/level1', 'views/digital-eco/level1/index', '', 1, '', 1, 1, 1),
(47, 45, '二级领域', '/digital-eco/level2', 'views/digital-eco/level2/index', '', 1, '', 2, 1, 1),
(48, 45, '详细设计审核', '/digital-eco/design-review', 'views/digital-eco/design-review/index', '', 1, '', 3, 1, 1),
(49, 45, '代码评审任务', '/digital-eco/code-review', 'views/digital-eco/code-review/index', '', 1, '', 4, 1, 1);

-- ====== 设置 (仅PLATFORM_ADMIN) ======
INSERT INTO sys_menu (id, parent_id, menu_name, path, component, icon, type, perms, sort, status, visible) VALUES
(40, 0, '设置', '', '', 'Tools', 0, '', 6, 1, 1),
(41, 40, '菜单管理', '/system/menu', 'views/system/menu/index', '', 1, 'system:menu:list', 1, 1, 1),
(42, 40, '角色管理', '/system/role', 'views/system/role/index', '', 1, 'system:role:list', 2, 1, 1),
(43, 40, '用户管理', '/system/user', 'views/system/user/index', '', 1, 'system:user:list', 3, 1, 1);

-- ====== 角色菜单分配 ======

-- PLATFORM_ADMIN: ALL
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu;

-- DEPT_ADMIN: 首页 + 技术管理(全部) + 资产管理 + 生产管理
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(2,1),
(2,10),(2,11),(2,12),(2,13),(2,14),(2,15),(2,16),(2,17),(2,18),(2,19),(2,20),(2,21),(2,22),
(2,25),(2,26),(2,27),(2,28),(2,29),(2,30),
(2,35),(2,36),
(2,45),(2,46),(2,47),(2,48),(2,49);

-- DEPT_DBA: 首页 + 数据库管理(全部) + 巡检任务 + 资产管理
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(3,1),
(3,10),(3,15),(3,16),(3,17),(3,18),(3,19),
(3,25),(3,26),(3,27),(3,28),(3,29),(3,30);

-- NORMAL_USER: 首页 + 技改任务管理(全部) + 数据库管理(只读巡检)
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(4,1),
(4,10),(4,11),(4,12),(4,13),(4,14),
(4,15),(4,18);

-- GUEST: 仅首页
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(5,1);
