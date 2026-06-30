package com.icbc.sh.techmg.business.service;

import java.util.List;
import java.util.Map;

/**
 * 文件解析结果 — 包含表头、列映射、未匹配列、数据行
 */
public class ParseResult {

    private final String[] headers;
    private final Map<String, Integer> mapping;
    private final List<String> unmatched;
    private final List<String[]> dataRows;

    public ParseResult(String[] headers, Map<String, Integer> mapping, List<String> unmatched, List<String[]> dataRows) {
        this.headers = headers;
        this.mapping = mapping;
        this.unmatched = unmatched;
        this.dataRows = dataRows;
    }

    public String[] getHeaders() {
        return headers;
    }

    public Map<String, Integer> getMapping() {
        return mapping;
    }

    public List<String> getUnmatched() {
        return unmatched;
    }

    public List<String[]> getDataRows() {
        return dataRows;
    }
}
