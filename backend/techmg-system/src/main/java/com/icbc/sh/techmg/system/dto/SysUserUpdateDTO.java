package com.icbc.sh.techmg.system.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SysUserUpdateDTO {
    @NotNull(message = "用户ID不能为空")
    private Long id;

    private String realName;
    private Long deptId;
    private String email;
    private String phone;
    private Integer status;
}
