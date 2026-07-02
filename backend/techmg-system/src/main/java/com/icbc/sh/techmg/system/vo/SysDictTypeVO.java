package com.icbc.sh.techmg.system.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SysDictTypeVO {
    private Long id;
    private String dictName;
    private String dictType;
    private Integer status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
