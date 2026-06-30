package com.icbc.sh.techmg.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.icbc.sh.techmg.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tech_reform_task")
public class TechReformTask extends BaseEntity {
    private String taskName;
    private String taskCategory;
    private String taskSubcategory;
    private String taskSource;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String taskOwner;
    private String status;
}
