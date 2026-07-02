package com.icbc.sh.techmg.system.vo;

import lombok.Data;

import java.util.List;

@Data
public class SysDeptVO {
    private Long id;
    private String deptName;
    private String deptCode;
    private Long parentId;
    private String ancestors;
    private Integer sort;
    private Integer status;
    private List<SysDeptVO> children;
}
