package com.icbc.sh.techmg.common.constant;

public interface CacheConstants {
    String TOKEN_PREFIX = "token:";
    String PERM_PREFIX = "perm:";
    String MENU_PREFIX = "menu:";
    String DICT_PREFIX = "dict:";
    String EXCEL_TASK_PREFIX = "excel:task:";

    long TOKEN_TTL = 86400;       // 24h (seconds)
    long PERM_TTL = 30;           // 30min (minutes)
    long MENU_TTL = 30;           // 30min
    long DICT_TTL = 60;           // 1h
    long EXCEL_TASK_TTL = 24;     // 24h (hours)
}
