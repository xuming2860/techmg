package com.icbc.sh.techmg.system.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SysUserVO {
    private Long id;
    private String userId;
    private String username;
    private String branchId;
    private String branchName;
    private String notesId;
    private LocalDateTime lastLoginTime;
    private Integer status;
    private LocalDateTime createTime;
}