package com.icbc.sh.techmg.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DbTypeEnum {
    MYSQL("MySQL"),
    POLARDB("PolarDB"),
    GAUSSDB("GaussDB");

    private final String label;
}
