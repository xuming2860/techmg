package com.icbc.sh.techmg.common.enums;

public enum ReformStatus {
    PENDING("待开始"),
    IN_PROGRESS("进行中"),
    COMPLETED("已完成"),
    CLOSED("已关闭");

    private final String label;

    ReformStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
