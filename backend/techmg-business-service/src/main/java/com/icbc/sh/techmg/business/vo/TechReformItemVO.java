package com.icbc.sh.techmg.business.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TechReformItemVO {
    private Long id;
    private Long subtaskId;
    private String applicationName;
    private String governanceItem;
    private String issueDescription;
    private String fixVersion;
    private String responsiblePerson;
    private String governancePlan;
    private String feedback;
    private String itemStatus;
    private String remark;
    private LocalDateTime createTime;
    private String createBy;
    private LocalDateTime updateTime;
    private String updateBy;
}
