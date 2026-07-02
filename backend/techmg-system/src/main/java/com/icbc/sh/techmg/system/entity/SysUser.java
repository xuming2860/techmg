package com.icbc.sh.techmg.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.icbc.sh.techmg.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {
    private String authNo;
    private String password;
    private String username;
    private String realName;
    private String email;
    private String phone;
    private Long deptId;
    private String adAccount;
    private String branchId;
    private String branchName;
    private String notesId;
    private java.time.LocalDateTime lastLoginTime;
    private Integer status;  // 0-禁用, 1-启用

    /** URL权限标识 — 仅内存透传，不持久化（默认 "all"） */
    @TableField(exist = false)
    private String urlPermission;
}
