package com.icbc.sh.techmg.business.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TechReformSubtaskCreateDTO {
    private Long parentTaskId;

    @NotBlank(message = "子任务名称不能为空")
    private String subtaskName;

    private String dbTypes;
    private String dataSource;
    private String description;
    private String departments;
    private String appScope;
    private Integer affectNegativeAsset;
    private LocalDate startDate;
    private LocalDate endDate;

    @NotBlank(message = "子任务状态不能为空")
    private String status;
}
