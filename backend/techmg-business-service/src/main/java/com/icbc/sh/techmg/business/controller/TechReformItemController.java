package com.icbc.sh.techmg.business.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.icbc.sh.techmg.business.entity.TechReformItem;
import com.icbc.sh.techmg.business.service.TechReformItemService;
import com.icbc.sh.techmg.common.constant.ResultCode;
import com.icbc.sh.techmg.common.model.R;
import com.icbc.sh.techmg.framework.excel.ExcelUtil;
import com.icbc.sh.techmg.framework.log.ApiAccessLog;
import com.icbc.sh.techmg.system.entity.SysUser;
import com.icbc.sh.techmg.system.service.SysUserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 技改治理项 Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/tech-reform/item")
@RequiredArgsConstructor
public class TechReformItemController {

    private final TechReformItemService techReformItemService;
    private final SysUserService sysUserService;

    private static final String ROLE_PLATFORM_ADMIN = "ROLE_PLATFORM_ADMIN";

    // Excel 列索引（导入/导出模板）
    private static final int COL_APP_NAME = 0;
    private static final int COL_GOVERNANCE_ITEM = 1;
    private static final int COL_ISSUE_DESC = 2;
    private static final int COL_FIX_VERSION = 3;
    private static final int COL_RESPONSIBLE = 4;
    private static final int COL_GOVERNANCE_PLAN = 5;
    private static final int COL_FEEDBACK = 6;
    private static final int COL_STATUS = 7;
    private static final int COL_REMARK = 8;

    /**
     * 分页查询治理项列表 — 非管理员只查看本部门的治理项
     */
    @ApiAccessLog
    @GetMapping("/list")
    public R<IPage<TechReformItem>> list(@RequestParam(defaultValue = "1") Integer page,
                                          @RequestParam(defaultValue = "10") Integer size,
                                          @RequestParam(required = false) Long subtaskId,
                                          @RequestParam(required = false) String appName,
                                          @RequestParam(required = false) String status,
                                          @RequestParam(required = false) String keyword) {
        Page<TechReformItem> pageReq = new Page<>(page, size);

        // 非平台管理员需要按部门过滤
        String branchId = getCurrentUserBranchId();
        log.debug("pageItems: subtaskId={}, appName={}, status={}, keyword={}, userBranchId={}",
                subtaskId, appName, status, keyword, branchId);

        // TODO: service 层需支持 branchId 参数以按部门过滤
        return R.ok(techReformItemService.pageItems(pageReq, subtaskId, appName, status, keyword));
    }

    /**
     * 创建治理项 — 平台管理员、部门管理员或部门DBA
     */
    @ApiAccessLog
    @PostMapping
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'DEPT_ADMIN', 'DEPT_DBA')")
    public R<TechReformItem> createItem(@RequestBody TechReformItem item) {
        techReformItemService.save(item);
        return R.ok(item);
    }

    /**
     * 更新治理项 — 平台管理员、部门管理员或部门DBA
     */
    @ApiAccessLog
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'DEPT_ADMIN', 'DEPT_DBA')")
    public R<TechReformItem> updateItem(@PathVariable Long id, @RequestBody TechReformItem item) {
        TechReformItem existing = techReformItemService.getById(id);
        if (existing == null) {
            return R.fail(ResultCode.NOT_FOUND, "治理项不存在");
        }
        item.setId(id);
        techReformItemService.updateById(item);
        return R.ok(item);
    }

    /**
     * 删除治理项 — 仅平台管理员
     */
    @ApiAccessLog
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    public R<Void> deleteItem(@PathVariable Long id) {
        TechReformItem existing = techReformItemService.getById(id);
        if (existing == null) {
            return R.fail(ResultCode.NOT_FOUND, "治理项不存在");
        }
        techReformItemService.removeById(id);
        return R.ok();
    }

    /**
     * 导入治理项 — 上传 Excel 文件（平台管理员或部门管理员）
     *
     * @param file      Excel 文件
     * @param mode      导入模式: overwrite(覆盖) / merge(合并)
     * @param subtaskId 子任务ID
     */
    @ApiAccessLog
    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'DEPT_ADMIN')")
    public R<Integer> importItems(@RequestParam("file") MultipartFile file,
                                   @RequestParam("mode") String mode,
                                   @RequestParam("subtaskId") Long subtaskId) throws IOException {
        if (file.isEmpty()) {
            return R.fail(ResultCode.PARAM_ERROR, "上传文件不能为空");
        }
        if (mode == null || mode.isBlank()) {
            return R.fail(ResultCode.PARAM_ERROR, "导入模式不能为空");
        }

        List<List<String>> rows = ExcelUtil.parseWorkbook(file.getInputStream());
        if (rows.size() <= 1) {
            return R.fail(ResultCode.PARAM_ERROR, "Excel 文件无数据行");
        }

        // 跳过表头，从第 2 行开始解析
        List<TechReformItem> items = new ArrayList<>();
        for (int i = 1; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            TechReformItem item = new TechReformItem();
            item.setSubtaskId(subtaskId);
            item.setApplicationName(getCell(row, COL_APP_NAME));
            item.setGovernanceItem(getCell(row, COL_GOVERNANCE_ITEM));
            item.setIssueDescription(getCell(row, COL_ISSUE_DESC));
            item.setFixVersion(getCell(row, COL_FIX_VERSION));
            item.setResponsiblePerson(getCell(row, COL_RESPONSIBLE));
            item.setGovernancePlan(getCell(row, COL_GOVERNANCE_PLAN));
            item.setRemark(getCell(row, COL_REMARK));
            item.setItemStatus("PENDING");
            items.add(item);
        }

        int count = techReformItemService.importItems(subtaskId, mode, items);
        log.info("导入治理项完成, subtaskId={}, mode={}, count={}", subtaskId, mode, count);
        return R.ok(count);
    }

    /**
     * 导出治理项 — 返回 Excel 文件
     */
    @ApiAccessLog
    @GetMapping("/export")
    public void exportItems(@RequestParam("subtaskId") Long subtaskId, HttpServletResponse response) throws IOException {
        List<TechReformItem> items = techReformItemService.exportItems(subtaskId);
        if (items.isEmpty()) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":404,\"message\":\"子任务下无治理项数据\"}");
            return;
        }

        String[] headers = {"应用名称", "治理项", "问题描述", "修改版本", "负责人",
                "治理计划", "完成情况", "状态", "备注"};

        Workbook wb = ExcelUtil.createWorkbook("治理项", headers);

        List<List<Object>> data = new ArrayList<>();
        for (TechReformItem item : items) {
            List<Object> row = new ArrayList<>();
            row.add(item.getApplicationName());
            row.add(item.getGovernanceItem());
            row.add(item.getIssueDescription());
            row.add(item.getFixVersion());
            row.add(item.getResponsiblePerson());
            row.add(item.getGovernancePlan());
            row.add(item.getFeedback());
            row.add(item.getItemStatus());
            row.add(item.getRemark());
            data.add(row);
        }
        ExcelUtil.fillRows(wb, "治理项", data, 1);

        String fileName = "治理项导出_" + subtaskId;
        ExcelUtil.writeToResponse(wb, fileName, response);
        wb.close();

        log.info("导出治理项完成, subtaskId={}, count={}", subtaskId, items.size());
    }

    /**
     * 批量更新治理项 — 上传 Excel 文件（平台管理员、部门管理员或部门DBA）
     * Excel 第一列为 ID（必填），其余列为待更新字段
     */
    @ApiAccessLog
    @PostMapping("/batch-update")
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'DEPT_ADMIN', 'DEPT_DBA')")
    public R<Integer> batchUpdateItems(@RequestParam("file") MultipartFile file,
                                       @RequestParam("subtaskId") Long subtaskId) throws IOException {
        if (file.isEmpty()) {
            return R.fail(ResultCode.PARAM_ERROR, "上传文件不能为空");
        }

        List<List<String>> rows = ExcelUtil.parseWorkbook(file.getInputStream());
        if (rows.size() <= 1) {
            return R.fail(ResultCode.PARAM_ERROR, "Excel 文件无数据行");
        }

        List<TechReformItem> items = new ArrayList<>();
        for (int i = 1; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            String idStr = getCell(row, 0);
            if (idStr == null || idStr.isBlank()) {
                continue; // 跳过无 ID 行
            }
            TechReformItem item = new TechReformItem();
            item.setId(Long.parseLong(idStr.trim()));
            item.setApplicationName(getCell(row, COL_APP_NAME));
            item.setGovernanceItem(getCell(row, COL_GOVERNANCE_ITEM));
            item.setIssueDescription(getCell(row, COL_ISSUE_DESC));
            item.setFixVersion(getCell(row, COL_FIX_VERSION));
            item.setResponsiblePerson(getCell(row, COL_RESPONSIBLE));
            item.setGovernancePlan(getCell(row, COL_GOVERNANCE_PLAN));
            item.setFeedback(getCell(row, COL_FEEDBACK));
            item.setItemStatus(getCell(row, COL_STATUS));
            item.setRemark(getCell(row, COL_REMARK));
            items.add(item);
        }

        if (items.isEmpty()) {
            return R.fail(ResultCode.PARAM_ERROR, "未解析到有效数据行");
        }

        int count = techReformItemService.batchUpdate(subtaskId, items);
        log.info("批量更新治理项完成, subtaskId={}, count={}", subtaskId, count);
        return R.ok(count);
    }

    // ---- helper methods ----

    private String getCell(List<String> row, int index) {
        if (index < row.size()) {
            String val = row.get(index);
            return (val != null && !val.isBlank()) ? val.trim() : null;
        }
        return null;
    }

    /**
     * 获取当前登录用户的分支ID。非管理员用户需按 branchId 过滤数据。
     * 返回 null 表示当前用户是平台管理员，不需要部门过滤。
     */
    private String getCurrentUserBranchId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        // 平台管理员不做部门过滤
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (ROLE_PLATFORM_ADMIN.equals(authority.getAuthority())) {
                return null;
            }
        }

        if (authentication.getPrincipal() instanceof UserDetails userDetails) {
            String authNo = userDetails.getUsername();
            SysUser sysUser = sysUserService.getByAuthNo(authNo);
            if (sysUser != null) {
                return sysUser.getBranchId();
            }
        }
        return null;
    }
}
