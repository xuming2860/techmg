package com.icbc.sh.techmg.system.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SysRoleVO {
    private Long id;
    private String roleName;
    private String roleCode;
    private String description;
    private Integer sort;
    private Integer status;
    private LocalDateTime createTime;
}
