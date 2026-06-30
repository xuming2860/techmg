package com.icbc.sh.techmg.business.controller;

import com.icbc.sh.techmg.framework.excel.ExcelUtil;
import com.icbc.sh.techmg.framework.log.ApiAccessLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * Excel 模板下载控制器
 */
@Slf4j
@Tag(name = "Excel模板")
@RestController
@RequestMapping("/api/excel")
public class ExcelController {

    @ApiAccessLog
    @Operation(summary = "下载Excel模板")
    @GetMapping("/template/{type}")
    public void downloadTemplate(@PathVariable String type, HttpServletResponse response) throws IOException {
        String[] headers;
        String sheetName;
        String fileName;

        switch (type) {
            case "governance":
                sheetName = "技术治理";
                fileName = "技术治理任务导入模板";
                headers = new String[]{"任务名称", "任务年度", "应用名称", "治理项", "问题描述",
                        "修改版本", "负责人", "备注"};
                break;
            case "db-inspection":
                sheetName = "数据库巡检";
                fileName = "数据库巡检任务导入模板";
                headers = new String[]{"巡检名称", "巡检类型(ROUTINE/EMERGENCY)", "数据库实例",
                        "检查项", "检查结果(PASS/FAIL/WARN)", "问题详情", "优化建议", "负责人", "备注"};
                break;
            case "asset":
                sheetName = "资产管理";
                fileName = "资产管理导入模板";
                headers = new String[]{"应用名称", "应用编码", "应用类型", "描述",
                        "参数名称", "参数值", "参数类型", "环境(DEV/TEST/PROD)", "参数说明"};
                break;
            default:
                response.setStatus(400);
                response.getWriter().write("{\"code\":400,\"message\":\"未知模板类型: " + type + "\"}");
                return;
        }

        Workbook wb = ExcelUtil.createTemplate(sheetName, headers);
        ExcelUtil.writeToResponse(wb, fileName, response);

        // 清理
        wb.close();
    }
}
