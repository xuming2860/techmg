package com.icbc.sh.techmg.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.icbc.sh.techmg.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tech_reform_item")
public class TechReformItem extends BaseEntity {
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
}
