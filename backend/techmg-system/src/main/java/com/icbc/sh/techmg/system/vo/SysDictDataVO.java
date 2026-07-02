package com.icbc.sh.techmg.system.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SysDictDataVO {
    private Long id;
    private String dictType;
    private String dictLabel;
    private String dictValue;
    private Integer sort;
    private Integer status;
    private String remark;
    private String cssClass;
    private LocalDateTime createTime;
}
