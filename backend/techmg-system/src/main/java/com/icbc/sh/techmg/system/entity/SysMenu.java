package com.icbc.sh.techmg.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.icbc.sh.techmg.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
public class SysMenu extends BaseEntity {
    private Long parentId;
    private String menuName;
    private String path;
    private String component;
    private String icon;
    private Integer type;    // 0-目录, 1-菜单, 2-按钮
    private String perms;
    private Integer sort;
    private Integer status;
    private Integer visible;
}
