package com.icbc.sh.techmg.system.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SysMenuVO {
    private Long id;
    private Long parentId;
    private String menuName;
    private String path;
    private String component;
    private String icon;
    private Integer type;
    private String perms;
    private Integer sort;
    private Integer status;
    private Integer visible;
    private String layout;
    private LocalDateTime createTime;
    private List<SysMenuVO> children;
}
