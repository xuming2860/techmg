package com.icbc.sh.techmg.business.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TechReformTaskVO {
    private Long id;
    private String taskName;
    private String taskCategory;
    private String taskSubcategory;
    private String taskSource;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String taskOwner;
    private String status;
    private LocalDateTime createTime;
    private String createBy;
    private LocalDateTime updateTime;
    private String updateBy;
}
