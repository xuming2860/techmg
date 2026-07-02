package com.icbc.sh.techmg.business.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TechReformItemUpdateDTO {
    @NotNull(message = "ID不能为空")
    private Long id;

    private Long subtaskId;

    @NotBlank(message = "应用名称不能为空")
    private String applicationName;

    @NotBlank(message = "治理项不能为空")
    private String governanceItem;

    private String issueDescription;
    private String fixVersion;
    private String responsiblePerson;
    private String governancePlan;
    private String feedback;

    @NotBlank(message = "治理项状态不能为空")
    private String itemStatus;

    private String remark;
}
