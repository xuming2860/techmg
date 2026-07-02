package com.icbc.sh.techmg.system.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SysUserVO {
    private Long id;
    private String authNo;
    private String username;
    private String realName;
    private String email;
    private String phone;
    private Long deptId;
    private Integer status;
    private String adAccount;
    private String branchId;
    private String branchName;
    private String notesId;
    private LocalDateTime lastLoginTime;
    private LocalDateTime createTime;
}
