package com.icbc.sh.techmg.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.icbc.sh.techmg.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dept")
public class SysDept extends BaseEntity {
    private String deptName;
    private String deptCode;
    private Long parentId;
    private String ancestors;
    private Integer sort;
    private Integer status;

    @TableField(exist = false)
    private List<SysDept> children;
}
