package com.icbc.sh.techmg.system.entity;

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
    private Integer status;  // 0-禁用, 1-启用
}
