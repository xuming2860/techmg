package com.icbc.sh.techmg.system.model.dto;

import lombok.Data;

@Data
public class UserQueryDTO {
    private Integer page;
    private Integer size;
    private String keyword;
}
