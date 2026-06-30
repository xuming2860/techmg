package com.icbc.sh.techmg.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.icbc.sh.techmg.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class SysRole extends BaseEntity {
    private String roleName;
    private String roleCode;
    private String description;
    private Integer sort;
    private Integer status;
}
