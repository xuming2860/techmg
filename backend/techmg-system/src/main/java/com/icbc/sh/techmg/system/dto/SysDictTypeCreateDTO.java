package com.icbc.sh.techmg.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SysDictTypeCreateDTO {
    @NotBlank(message = "字典名称不能为空")
    private String dictName;

    @NotBlank(message = "字典类型不能为空")
    private String dictType;

    private Integer status;
    private String remark;
}
