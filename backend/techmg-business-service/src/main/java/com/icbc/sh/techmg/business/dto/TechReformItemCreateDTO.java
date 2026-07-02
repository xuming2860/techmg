package com.icbc.sh.techmg.business.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TechReformItemCreateDTO {
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
