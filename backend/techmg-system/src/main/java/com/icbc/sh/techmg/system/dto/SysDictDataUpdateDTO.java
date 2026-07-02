package com.icbc.sh.techmg.system.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SysDictDataUpdateDTO {
    @NotNull(message = "ID不能为空")
    private Long id;

    private String dictType;
    private String dictLabel;
    private String dictValue;
    private Integer sort;
    private Integer status;
    private String remark;
    private String cssClass;
}
