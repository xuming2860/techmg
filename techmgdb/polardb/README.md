# PolarDB SQL 变更管理

> 所有数据库变更 SQL **必须**独立于后端代码，放入本目录按版本管理。

## 目录结构

```
techmgdb/polardb/
├── README.md              # 本文件
├── v1.0/
│   ├── ddl/               # 表结构变更 (CREATE/ALTER/DROP TABLE)
│   ├── dml/               # 数据变更 (INSERT/UPDATE/DELETE)
│   └── rollback/          # 对应回滚脚本
├── v2.0/
│   ├── ddl/
│   ├── dml/
│   └── rollback/
└── v{version}/
    ├── ddl/
    ├── dml/
    └── rollback/
```

## 命名规范

### DDL
```
{序号}_{动作}_{对象}.sql
示例: 01_create_table_tech_reform_task.sql
      02_alter_table_sys_user_add_column.sql
```

### DML
```
{序号}_{动作}_{对象}.sql
示例: 01_insert_dict_data.sql
      02_update_default_roles.sql
```

### Rollback
```
{序号}_rollback_{对象}.sql
示例: 01_rollback_tech_reform_task.sql
```

## 规则

1. **变更必须可回滚**：每个 DDL/DML 必须提供对应的 rollback 脚本
2. **按版本隔离**：每个版本的 SQL 放入对应版本目录
3. **禁止嵌入后端代码**：SQL 不得放入 `backend/` 的 `src/main/resources/` 中
4. **序号保证执行顺序**：同目录下以数字序号前缀保证执行顺序
5. **幂等性**：DDL 应使用 `IF EXISTS` / `IF NOT EXISTS` 确保可重复执行
