package com.icbc.sh.techmg.business.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.icbc.sh.techmg.business.entity.TechReformSubtask;
import com.icbc.sh.techmg.business.service.TechReformSubtaskService;
import com.icbc.sh.techmg.common.constant.ResultCode;
import com.icbc.sh.techmg.common.model.R;
import com.icbc.sh.techmg.framework.log.ApiAccessLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 技改子任务 Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/tech-reform/subtask")
@RequiredArgsConstructor
public class TechReformSubtaskController {

    private final TechReformSubtaskService techReformSubtaskService;

    /**
     * 分页查询子任务列表
     */
    @ApiAccessLog
    @GetMapping("/list")
    public R<IPage<TechReformSubtask>> list(@RequestParam(defaultValue = "1") Integer page,
                                             @RequestParam(defaultValue = "10") Integer size,
                                             @RequestParam(required = false) Long parentTaskId,
                                             @RequestParam(required = false) String status) {
        Page<TechReformSubtask> pageReq = new Page<>(page, size);
        return R.ok(techReformSubtaskService.pageSubtasks(pageReq, parentTaskId, status));
    }

    /**
     * 查询子任务详情
     */
    @ApiAccessLog
    @GetMapping("/{id}")
    public R<TechReformSubtask> getSubtask(@PathVariable Long id) {
        TechReformSubtask subtask = techReformSubtaskService.getById(id);
        if (subtask == null) {
            return R.fail(ResultCode.NOT_FOUND, "子任务不存在");
        }
        return R.ok(subtask);
    }

    /**
     * 创建子任务 — 平台管理员或部门管理员
     */
    @ApiAccessLog
    @PostMapping
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'DEPT_ADMIN')")
    public R<TechReformSubtask> createSubtask(@RequestBody TechReformSubtask subtask) {
        techReformSubtaskService.save(subtask);
        return R.ok(subtask);
    }

    /**
     * 更新子任务
     */
    @ApiAccessLog
    @PutMapping("/{id}")
    public R<TechReformSubtask> updateSubtask(@PathVariable Long id, @RequestBody TechReformSubtask subtask) {
        TechReformSubtask existing = techReformSubtaskService.getById(id);
        if (existing == null) {
            return R.fail(ResultCode.NOT_FOUND, "子任务不存在");
        }
        subtask.setId(id);
        techReformSubtaskService.updateById(subtask);
        return R.ok(subtask);
    }

    /**
     * 更新子任务状态 — 使用 ReformStatus 枚举值
     */
    @ApiAccessLog
    @PutMapping("/{id}/status")
    public R<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        TechReformSubtask existing = techReformSubtaskService.getById(id);
        if (existing == null) {
            return R.fail(ResultCode.NOT_FOUND, "子任务不存在");
        }
        String status = body.get("status");
        if (status == null || status.isBlank()) {
            return R.fail(ResultCode.PARAM_ERROR, "状态值不能为空");
        }
        techReformSubtaskService.updateStatus(id, status);
        return R.ok();
    }

    /**
     * 删除子任务 — 仅平台管理员
     */
    @ApiAccessLog
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    public R<Void> deleteSubtask(@PathVariable Long id) {
        TechReformSubtask existing = techReformSubtaskService.getById(id);
        if (existing == null) {
            return R.fail(ResultCode.NOT_FOUND, "子任务不存在");
        }
        techReformSubtaskService.removeById(id);
        return R.ok();
    }
}
