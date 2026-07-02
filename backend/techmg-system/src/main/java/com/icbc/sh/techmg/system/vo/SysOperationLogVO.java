package com.icbc.sh.techmg.system.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SysOperationLogVO {
    private Long id;
    private String module;
    private String operation;
    private String operator;
    private String requestParam;
    private String responseResult;
    private String ip;
    private Long costTime;
    private Integer status;
    private LocalDateTime createTime;
}
