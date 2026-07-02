package com.icbc.sh.techmg.business.service.impl;

import com.icbc.sh.techmg.business.entity.TechReformItem;
import com.icbc.sh.techmg.business.service.ParseResult;
import com.icbc.sh.techmg.business.service.TechReformItemService;
import com.icbc.sh.techmg.common.exception.BusinessException;
import com.icbc.sh.techmg.framework.excel.ExcelUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 技改任务 Excel 导入导出服务 — 支持 CSV/TXT/Excel 解析、导出、批量更新
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TechReformExcelServiceImpl {

    private final TechReformItemService techReformItemService;

    /** 导出模板表头 */
    private static final String[] EXPORT_HEADERS = {
            "应用名", "治理项", "问题描述", "修改版本", "负责人",
            "治理计划", "反馈", "状态", "备注"
    };

    // ========== Public API ==========

    /**
     * Step 1: 解析上传文件（CSV/TXT/Excel），自动匹配列名
     *
     * @param file 上传文件
     * @return ParseResult 包含表头、列映射、未匹配列、数据行
     */
    public ParseResult parseFile(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null || filename.isBlank()) {
            throw new BusinessException(1001, "文件名不能为空");
        }

        String ext = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        List<String[]> rawData;

        try {
            switch (ext) {
                case "csv":
                case "txt":
                    rawData = parseTextFile(file, ext);
                    break;
                case "xls":
                case "xlsx":
                    rawData = parseExcelFile(file);
                    break;
                default:
                    throw new BusinessException(1001, "不支持的文件格式: " + ext);
            }
        } catch (IOException e) {
            log.error("文件解析失败: {}", filename, e);
            throw new BusinessException(1001, "文件解析失败: " + e.getMessage());
        }

        if (rawData.isEmpty()) {
            throw new BusinessException(1001, "文件为空");
        }

        String[] headers = rawData.get(0);
        Map<String, Integer> mapping = autoMatch(headers);
        List<String> unmatched = getUnmatched(headers, mapping);
        List<String[]> dataRows = rawData.subList(1, rawData.size());

        return new ParseResult(headers, mapping, unmatched, dataRows);
    }

    /**
     * Step 2: 导出治理项列表为 .xlsx 字节数组
     * <p>
     * 表头样式：粗体 + 浅蓝色背景；自动列宽。
     *
     * @param items 治理项列表
     * @return .xlsx 工作簿字节数组
     */
    public byte[] exportToExcel(List<TechReformItem> items) {
        XSSFWorkbook wb = new XSSFWorkbook();
        try {
            Sheet sheet = wb.createSheet("治理项");

            // ---- 表头行（粗体 + 浅蓝背景） ----
            CellStyle headerStyle = createHeaderStyle(wb);
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < EXPORT_HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(EXPORT_HEADERS[i]);
                cell.setCellStyle(headerStyle);
            }

            // ---- 数据行 ----
            for (int i = 0; i < items.size(); i++) {
                TechReformItem item = items.get(i);
                Row row = sheet.createRow(i + 1);
                setCellValue(row.createCell(0), item.getApplicationName());
                setCellValue(row.createCell(1), item.getGovernanceItem());
                setCellValue(row.createCell(2), item.getIssueDescription());
                setCellValue(row.createCell(3), item.getFixVersion());
                setCellValue(row.createCell(4), item.getResponsiblePerson());
                setCellValue(row.createCell(5), item.getGovernancePlan());
                setCellValue(row.createCell(6), item.getFeedback());
                setCellValue(row.createCell(7), item.getItemStatus());
                setCellValue(row.createCell(8), item.getRemark());
            }

            // ---- 自动列宽 ----
            for (int i = 0; i < EXPORT_HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // ---- 写入字节数组 ----
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            wb.write(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("导出Excel失败", e);
            throw new BusinessException(1001, "导出Excel失败: " + e.getMessage());
        } finally {
            try {
                wb.close();
            } catch (IOException e) {
                log.warn("关闭Workbook失败", e);
            }
        }
    }

    /**
     * Step 3: 批量更新 — 解析上传 Excel，按 (applicationName + governanceItem) 匹配并更新
     * <p>
     * 支持局部更新：仅更新 Excel 中存在的「治理计划」「反馈」「负责人」列。
     *
     * @param subtaskId 子任务ID
     * @param file      上传 Excel 文件
     * @return 更新记录数
     */
    @Transactional(rollbackFor = Exception.class)
    public int batchUpdateFromExcel(Long subtaskId, MultipartFile file) {
        ParseResult parseResult = parseFile(file);
        Map<String, Integer> mapping = parseResult.getMapping();

        if (!mapping.containsKey("applicationName") || !mapping.containsKey("governanceItem")) {
            throw new BusinessException(1001, "Excel 文件缺少必要的列: 应用名称、治理项");
        }

        // 获取子任务下现有治理项
        List<TechReformItem> existingItems = techReformItemService.exportItems(subtaskId);
        if (existingItems.isEmpty()) {
            return 0;
        }

        // 构建查询 Map: "applicationName|governanceItem" → TechReformItem
        Map<String, TechReformItem> itemMap = new HashMap<>();
        for (TechReformItem item : existingItems) {
            String key = buildItemKey(item.getApplicationName(), item.getGovernanceItem());
            itemMap.put(key, item);
        }

        int count = 0;
        for (String[] row : parseResult.getDataRows()) {
            String appName = getCellValue(row, mapping.get("applicationName"));
            String govItem = getCellValue(row, mapping.get("governanceItem"));
            if (appName == null || govItem == null) {
                continue;
            }

            String key = buildItemKey(appName, govItem);
            TechReformItem existing = itemMap.get(key);
            if (existing == null) {
                log.debug("未匹配到治理项, applicationName={}, governanceItem={}", appName, govItem);
                continue;
            }

            // 局部更新：仅更新 Excel 中存在的列
            if (mapping.containsKey("governancePlan")) {
                String val = getCellValue(row, mapping.get("governancePlan"));
                if (val != null) {
                    existing.setGovernancePlan(val);
                }
            }
            if (mapping.containsKey("feedback")) {
                String val = getCellValue(row, mapping.get("feedback"));
                if (val != null) {
                    existing.setFeedback(val);
                }
            }
            if (mapping.containsKey("responsiblePerson")) {
                String val = getCellValue(row, mapping.get("responsiblePerson"));
                if (val != null) {
                    existing.setResponsiblePerson(val);
                }
            }

            techReformItemService.updateById(existing);
            count++;
        }

        log.info("批量更新治理项完成, subtaskId={}, count={}", subtaskId, count);
        return count;
    }

    // ========== Private: 列名自动匹配 ==========

    /**
     * 将表头数组与实体字段做模糊匹配（中文别名 + 英文别名）
     */
    private Map<String, Integer> autoMatch(String[] headers) {
        Map<String, String> aliases = buildAliasMap();
        Map<String, Integer> mapping = new LinkedHashMap<>();
        for (int i = 0; i < headers.length; i++) {
            String header = headers[i].trim();
            String field = aliases.get(header);
            if (field != null) {
                // 一个字段只记录第一次出现的列
                mapping.putIfAbsent(field, i);
            }
        }
        return mapping;
    }

    /** 别名 → 实体字段名 */
    private Map<String, String> buildAliasMap() {
        Map<String, String> map = new LinkedHashMap<>();
        // applicationName
        map.put("application_name", "applicationName");
        map.put("applicationName", "applicationName");
        map.put("应用名", "applicationName");
        map.put("应用名称", "applicationName");
        map.put("应用", "applicationName");
        // governanceItem
        map.put("governance_item", "governanceItem");
        map.put("governanceItem", "governanceItem");
        map.put("治理项", "governanceItem");
        // issueDescription
        map.put("issue_description", "issueDescription");
        map.put("issueDescription", "issueDescription");
        map.put("问题描述", "issueDescription");
        map.put("issue", "issueDescription");
        // fixVersion
        map.put("fix_version", "fixVersion");
        map.put("fixVersion", "fixVersion");
        map.put("修改版本", "fixVersion");
        // responsiblePerson
        map.put("responsible_person", "responsiblePerson");
        map.put("responsiblePerson", "responsiblePerson");
        map.put("负责人", "responsiblePerson");
        map.put("责任人", "responsiblePerson");
        // governancePlan
        map.put("governance_plan", "governancePlan");
        map.put("governancePlan", "governancePlan");
        map.put("治理计划", "governancePlan");
        map.put("plan", "governancePlan");
        // feedback
        map.put("feedback", "feedback");
        map.put("feedback_content", "feedback");
        map.put("完成情况", "feedback");
        map.put("反馈", "feedback");
        // itemStatus
        map.put("item_status", "itemStatus");
        map.put("itemStatus", "itemStatus");
        map.put("状态", "itemStatus");
        map.put("status", "itemStatus");
        // remark
        map.put("remark", "remark");
        map.put("备注", "remark");
        return map;
    }

    /** 返回无法匹配到实体字段的列名列表 */
    private List<String> getUnmatched(String[] headers, Map<String, Integer> mapping) {
        Set<Integer> matchedIndices = new HashSet<>(mapping.values());
        List<String> unmatched = new ArrayList<>();
        for (int i = 0; i < headers.length; i++) {
            if (!matchedIndices.contains(i)) {
                unmatched.add(headers[i]);
            }
        }
        return unmatched;
    }

    // ========== Private: 文件解析 ==========

    /** 解析 CSV / TXT 文本文件 */
    private List<String[]> parseTextFile(MultipartFile file, String ext) throws IOException {
        byte[] bytes = file.getBytes();
        String content = new String(bytes, StandardCharsets.UTF_8);
        // 去除 UTF-8 BOM
        if (!content.isEmpty() && content.charAt(0) == '﻿') {
            content = content.substring(1);
        }
        String[] lines = content.split("\\r?\\n");
        List<String[]> result = new ArrayList<>();
        boolean isCsv = "csv".equals(ext);
        for (String line : lines) {
            if (line.trim().isEmpty()) {
                continue;
            }
            result.add(isCsv ? parseCsvLine(line) : line.split("\t", -1));
        }
        return result;
    }

    /** 解析单行 CSV（支持双引号转义及含逗号的字段） */
    private String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    sb.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                fields.add(sb.toString().trim());
                sb = new StringBuilder();
            } else {
                sb.append(c);
            }
        }
        fields.add(sb.toString().trim());
        return fields.toArray(new String[0]);
    }

    /** 解析 Excel 文件，复用 ExcelUtil 的通用解析方法 */
    private List<String[]> parseExcelFile(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream()) {
            List<List<String>> rows = ExcelUtil.parseWorkbook(is);
            List<String[]> result = new ArrayList<>();
            for (List<String> row : rows) {
                result.add(row.toArray(new String[0]));
            }
            return result;
        }
    }

    // ========== Private: 样式与单元格 ==========

    /** 创建表头样式：粗体 + 浅蓝色背景 + 边框 */
    private CellStyle createHeaderStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    /** 设置单元格字符串值（null 处理为空串） */
    private void setCellValue(Cell cell, String value) {
        cell.setCellValue(value != null ? value : "");
    }

    /** 从数据行中按索引取字符串值，越界或空值返回 null */
    private String getCellValue(String[] row, int index) {
        if (index >= 0 && index < row.length) {
            String val = row[index];
            return (val != null && !val.isBlank()) ? val.trim() : null;
        }
        return null;
    }

    /** 构建 (applicationName + governanceItem) 联合键 */
    private String buildItemKey(String appName, String govItem) {
        return (appName != null ? appName.trim() : "") + "|"
                + (govItem != null ? govItem.trim() : "");
    }
}
