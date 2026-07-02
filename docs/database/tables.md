# 数据库表资产清单

> 库名: `tmvp` / 用户: `techmg` / PolarDB 标准版 (MySQL 兼容)
>
> **维护规则**: 新增/修改/删除表时，必须同步更新本文档。AI 在涉及表结构变更时自动追加变更记录。

## 表清单

### 一、RBAC 权限系统表（6 张）

| 表名 | 含义 | Java 实体 | 功能模块 | 说明 |
|------|------|-----------|----------|------|
| `sys_user` | 用户表 | `SysUser.java` | 设置→用户管理 + 登录认证 | userId(12位)/username(中文名)/branchId/branchName/notesId(邮箱)/lastLoginTime/status |
| `sys_role` | 角色表 | `SysRole.java` | 设置→角色管理 | PLATFORM_ADMIN / DEPT_ADMIN / DEPT_DBA / NORMAL_USER / GUEST |
| `sys_user_role` | 用户角色关联 | `SysUserRole.java` | 设置→用户管理 | 用户←→角色 多对多 |
| `sys_menu` | 菜单表 | `SysMenu.java` | 设置→菜单管理 | 25 菜单项，3 级树（目录→菜单→按钮），type=0/1/2 |
| `sys_role_menu` | 角色菜单关联 | `SysRoleMenu.java` | 设置→角色管理 | 按角色分配菜单权限 |
| `sys_operation_log` | 操作日志表 | `SysOperationLog.java` | 全局 AOP | 模块/操作/IP/耗时/状态 |

### 二、系统扩展表（3 张）

| 表名 | 含义 | Java 实体 | 功能模块 | 说明 |
|------|------|-----------|----------|------|
| `sys_user_branch` | 用户多机构关联 | `SysUserBranch.java` | 登录→syncUserInfo | SSIC 返回的 BRANCHID_LIST，一个用户可属多机构 |
| `sys_dict_type` | 字典类型 | `SysDictType.java` | 全局数据字典 | 如 task_category / task_subcategory / task_source |
| `sys_dict_data` | 字典数据 | `SysDictData.java` | 全局数据字典 | 键值对，如「数据库治理→database_governance」 |

### 三、技改任务业务表（3 张）— v2.0

| 表名 | 含义 | Java 实体 | 功能模块 | 说明 |
|------|------|-----------|----------|------|
| `tech_reform_task` | 技改父任务 | `TechReformTask.java` | 技术管理→任务总览 | 任务名/大类小类/来源/描述/日期/牵头人/状态(PENDING→CLOSED) |
| `tech_reform_subtask` | 技改子任务 | `TechReformSubtask.java` | 技术管理→子任务管理 | 关联父任务，dbTypes/dataSource/departments/appScope/affectNegativeAsset |
| `tech_reform_item` | 治理清单条目 | `TechReformItem.java` | 技术管理→治理清单 | 应用名/治理项/问题描述/修复版本/负责人/反馈/状态 |

### 四、占位业务表（9 张）— v1.0 DDL，无实体

| 表名 | 含义 | 对应前端模块 | 状态 |
|------|------|-------------|------|
| `tech_governance_task` | 技术治理任务 | 技术管理→技术栈管理 | 占位 |
| `tech_governance_item` | 技术治理条目 | 技术管理→技术栈管理 | 占位 |
| `db_governance_task` | 数据库治理任务 | 技术管理→数据库管理 | 占位 |
| `db_governance_item` | 数据库治理条目 | 技术管理→数据库管理 | 占位 |
| `db_inspection_task` | 巡检任务 | 技术管理→巡检任务管理 | 占位 |
| `db_inspection_item` | 巡检条目 | 技术管理→巡检任务管理 | 占位 |
| `asset_application` | 资产应用 | 资产管理→应用信息管理 | 占位 |
| `asset_param` | 资产参数 | 资产管理→应用资产健康度 | 占位 |
| `asset_code` | 资产编码 | 资产管理 | 占位 |
| `asset_procedure` | 资产过程 | 资产管理 | 占位 |

---

## 变更记录

| 日期 | 版本 | 变更 | 说明 |
|------|------|------|------|
| 2025-06 | v1.0 | 新增 22 表 | 系统表 7 + 业务表 15（含 10 占位），后 sys_dept 在 v2.1 移除 |
| 2026-07 | v2.1 | 移除 `sys_dept` | 前端未使用，SSIC 用 branch 体系替代部门管理 |
| 2026-07 | v2.1 | 重构 `sys_user` | auth_no→user_id(12位), 删password/real_name/email/phone/ad_account, username=中文名, notes_id=邮箱 |
| 2026-06 | v2.0 | ALTER `sys_user` +5列 | ad_account/branch_id/branch_name/notes_id/last_login_time |
| 2026-06 | v2.0 | 新增 4 表 | sys_user_branch + tech_reform_task/subtask/item |
| 2026-07 | v2.1 | SSIC 改造 | 无 DDL 变更（urlPermission/saeRole 均为内存字段，不持久化） |