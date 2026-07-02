package com.icbc.sh.techmg.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SysDeptUpdateDTO {
    @NotNull(message = "ID不能为空")
    private Long id;

    @NotBlank(message = "部门名称不能为空")
    private String deptName;

    @NotBlank(message = "部门编码不能为空")
    private String deptCode;

    private Long parentId;
    private String ancestors;
    private Integer sort;
    private Integer status;
}
