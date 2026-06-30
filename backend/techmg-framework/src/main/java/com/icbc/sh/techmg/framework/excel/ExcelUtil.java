package com.icbc.sh.techmg.framework.excel;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel 工具类 — 基于 Apache POI
 */
public class ExcelUtil {

    private ExcelUtil() {
    }

    /**
     * 创建带表头的工作簿
     *
     * @param sheetName 工作表名称
     * @param headers   表头数组
     * @return Workbook
     */
    public static Workbook createWorkbook(String sheetName, String[] headers) {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet(sheetName);
        if (headers != null && headers.length > 0) {
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
        }
        return wb;
    }

    /**
     * 填充数据行
     *
     * @param wb        工作簿
     * @param sheetName 工作表名称
     * @param data      数据（外层 List 为行，内层 List 为单元格值）
     * @param startRow  起始行号（0-based）
     */
    public static void fillRows(Workbook wb, String sheetName, List<List<Object>> data, int startRow) {
        Sheet sheet = wb.getSheet(sheetName);
        if (sheet == null || data == null) {
            return;
        }
        for (int i = 0; i < data.size(); i++) {
            Row row = sheet.createRow(startRow + i);
            List<Object> rowData = data.get(i);
            if (rowData != null) {
                for (int j = 0; j < rowData.size(); j++) {
                    Cell cell = row.createCell(j);
                    setCellValue(cell, rowData.get(j));
                }
            }
        }
    }

    /**
     * 解析工作簿 — 读取所有行内容为字符串列表
     *
     * @param is 输入流
     * @return List&lt;List&lt;String&gt;&gt; 每行单元格值
     */
    public static List<List<String>> parseWorkbook(InputStream is) throws IOException {
        List<List<String>> result = new ArrayList<>();
        try (Workbook wb = WorkbookFactory.create(is)) {
            for (int s = 0; s < wb.getNumberOfSheets(); s++) {
                Sheet sheet = wb.getSheetAt(s);
                for (Row row : sheet) {
                    List<String> rowData = new ArrayList<>();
                    for (Cell cell : row) {
                        rowData.add(getCellStringValue(cell));
                    }
                    // 跳过全空行
                    if (rowData.stream().anyMatch(v -> v != null && !v.isEmpty())) {
                        result.add(rowData);
                    }
                }
            }
        }
        return result;
    }

    /**
     * 创建模板工作簿（带表头，含样式）
     *
     * @param sheetName 工作表名称
     * @param headers   表头数组
     * @return Workbook
     */
    public static Workbook createTemplate(String sheetName, String[] headers) {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet(sheetName);

        if (headers != null && headers.length > 0) {
            CellStyle headerStyle = createHeaderStyle(wb);
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.autoSizeColumn(i);
            }
            // 冻结首行
            sheet.createFreezePane(0, 1);
        }
        return wb;
    }

    /**
     * 将工作簿写入 HTTP 响应（下载）
     *
     * @param wb       工作簿
     * @param fileName 下载文件名（不含 .xlsx 后缀）
     * @param response HttpServletResponse
     */
    public static void writeToResponse(Workbook wb, String fileName, HttpServletResponse response) throws IOException {
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition",
                "attachment; filename=" + encodedFileName + ".xlsx");
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");

        try (OutputStream os = response.getOutputStream()) {
            wb.write(os);
            os.flush();
        }
    }

    // ========== private helpers ==========

    private static CellStyle createHeaderStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private static void setCellValue(Cell cell, Object value) {
        if (value == null) {
            cell.setCellValue("");
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue(value.toString());
        }
    }

    private static String getCellStringValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toString();
                }
                // 避免科学计数法
                double numVal = cell.getNumericCellValue();
                if (numVal == Math.floor(numVal) && !Double.isInfinite(numVal)) {
                    return String.valueOf((long) numVal);
                }
                return String.valueOf(numVal);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BLANK:
            default:
                return "";
        }
    }
}
