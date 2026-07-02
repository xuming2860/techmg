package com.icbc.sh.techmg.system.dto;

import lombok.Data;

@Data
public class SysUserQueryDTO {
    private Integer page;
    private Integer size;
    private String keyword;
}
