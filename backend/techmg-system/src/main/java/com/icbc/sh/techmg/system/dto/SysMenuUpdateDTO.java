package com.icbc.sh.techmg.system.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SysMenuUpdateDTO {
    @NotNull(message = "ID不能为空")
    private Long id;

    private Long parentId;
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
