package com.icbc.sh.techmg.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SysUserCreateDTO {
    @NotBlank(message = "认证号不能为空")
    private String authNo;

    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    private String username;
    private Long deptId;
    private String email;
    private String phone;
}
