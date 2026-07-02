package com.icbc.sh.techmg.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SysUserCreateDTO {
    @NotBlank(message = "认证号不能为空")
    private String userId;

    @NotBlank(message = "姓名不能为空")
    private String username;

    private String notesId;
}