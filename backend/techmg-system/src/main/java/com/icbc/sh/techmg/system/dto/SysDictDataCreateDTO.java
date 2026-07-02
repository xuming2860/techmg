package com.icbc.sh.techmg.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SysDictDataCreateDTO {
    @NotBlank(message = "字典类型不能为空")
    private String dictType;

    @NotBlank(message = "字典标签不能为空")
    private String dictLabel;

    @NotBlank(message = "字典值不能为空")
    private String dictValue;

    private Integer sort;
    private Integer status;
    private String remark;
    private String cssClass;
}
