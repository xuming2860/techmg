package com.icbc.sh.techmg.business.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TechReformTaskCreateDTO {
    @NotBlank(message = "任务名称不能为空")
    private String taskName;

    private String taskCategory;
    private String taskSubcategory;
    private String taskSource;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String taskOwner;

    @NotBlank(message = "任务状态不能为空")
    private String status;
}
