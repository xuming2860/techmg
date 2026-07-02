package com.icbc.sh.techmg.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InspectionTypeEnum {
    ROUTINE("常规巡检"),
    EMERGENCY("紧急巡检");

    private final String label;
}
