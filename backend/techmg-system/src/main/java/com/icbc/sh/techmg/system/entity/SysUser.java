package com.icbc.sh.techmg.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.icbc.sh.techmg.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {
    private String userId;       // 统一认证号, 12位数字
    private String username;     // 用户中文名 (即 SSIC TELLERNAME)
    private String branchId;
    private String branchName;
    private String notesId;      // 邮箱 (即 SSIC NOTESID)
    private java.time.LocalDateTime lastLoginTime;
    private Integer status;      // 0-禁用, 1-启用
}