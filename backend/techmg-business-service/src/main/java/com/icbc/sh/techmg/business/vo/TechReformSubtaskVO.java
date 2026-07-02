package com.icbc.sh.techmg.business.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TechReformSubtaskVO {
    private Long id;
    private Long parentTaskId;
    private String subtaskName;
    private String dbTypes;
    private String dataSource;
    private String description;
    private String departments;
    private String appScope;
    private Integer affectNegativeAsset;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private LocalDateTime createTime;
    private String createBy;
    private LocalDateTime updateTime;
    private String updateBy;
}
