package com.icbc.sh.techmg.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SysMenuCreateDTO {
    private Long parentId;

    @NotBlank(message = "菜单名称不能为空")
    private String menuName;

    private String path;
    private String component;
    private String icon;
    private Integer type;
    private String perms;
    private Integer sort;
    private Integer status;
    private Integer visible;
}
