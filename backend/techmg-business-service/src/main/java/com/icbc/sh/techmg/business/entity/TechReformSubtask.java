package com.icbc.sh.techmg.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.icbc.sh.techmg.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tech_reform_subtask")
public class TechReformSubtask extends BaseEntity {
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
}
