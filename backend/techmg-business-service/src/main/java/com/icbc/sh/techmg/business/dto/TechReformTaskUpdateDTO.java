package com.icbc.sh.techmg.business.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TechReformTaskUpdateDTO {
    @NotNull(message = "ID不能为空")
    private Long id;

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
