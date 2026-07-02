package com.icbc.sh.techmg.system.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SysRoleUpdateDTO {
    @NotNull(message = "ID不能为空")
    private Long id;

    private String roleName;
    private String roleCode;
    private String description;
    private Integer sort;
    private Integer status;
}
