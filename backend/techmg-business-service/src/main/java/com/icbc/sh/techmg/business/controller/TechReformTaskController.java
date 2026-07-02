package com.icbc.sh.techmg.business.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.icbc.sh.techmg.business.dto.TechReformTaskCreateDTO;
import com.icbc.sh.techmg.business.dto.TechReformTaskUpdateDTO;
import com.icbc.sh.techmg.business.entity.TechReformTask;
import com.icbc.sh.techmg.business.service.TechReformTaskService;
import com.icbc.sh.techmg.business.vo.TechReformTaskVO;
import com.icbc.sh.techmg.common.model.R;
import com.icbc.sh.techmg.common.util.PageResult;
import com.icbc.sh.techmg.framework.log.ApiAccessLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 技改任务 Controller
 */
@Slf4j
@Tag(name = "技改任务管理")
@RestController
@RequestMapping("/api/tech-reform/task")
@RequiredArgsConstructor
public class TechReformTaskController {

    private final TechReformTaskService techReformTaskService;

    /**
     * 分页查询任务列表
     */
    @Operation(summary = "分页查询任务列表")
    @ApiAccessLog
    @GetMapping("/list")
    public R<PageResult<TechReformTaskVO>> list(@RequestParam(name = "page", defaultValue = "1") Integer page,
                                           @RequestParam(name = "size", defaultValue = "10") Integer size,
                                           @RequestParam(name = "keyword", required = false) String keyword,
                                           @RequestParam(name = "category", required = false) String category,
                                           @RequestParam(name = "status", required = false) String status) {
        Page<TechReformTask> pageReq = new Page<>(page, size);
        return R.ok(PageResult.of(techReformTaskService.pageTasks(pageReq, keyword, category, status)));
    }

    /**
     * 查询任务详情
     */
    @Operation(summary = "查询任务详情")
    @ApiAccessLog
    @GetMapping("/{id}")
    public R<TechReformTaskVO> getTask(@PathVariable("id") Long id) {
        return R.ok(techReformTaskService.getTaskVO(id));
    }

    /**
     * 创建任务 — 仅平台管理员
     */
    @Operation(summary = "创建任务")
    @ApiAccessLog
    @PostMapping
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    public R<TechReformTaskVO> createTask(@Valid @RequestBody TechReformTaskCreateDTO dto) {
        return R.ok(techReformTaskService.saveTask(dto));
    }

    /**
     * 更新任务 — 仅平台管理员
     */
    @Operation(summary = "更新任务")
    @ApiAccessLog
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    public R<TechReformTaskVO> updateTask(@PathVariable("id") Long id, @Valid @RequestBody TechReformTaskUpdateDTO dto) {
        dto.setId(id);
        return R.ok(techReformTaskService.updateTask(dto));
    }

    /**
     * 更新任务状态 — 仅平台管理员
     */
    @Operation(summary = "更新任务状态")
    @ApiAccessLog
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    public R<Void> updateStatus(@PathVariable("id") Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        techReformTaskService.updateStatus(id, status);
        return R.ok();
    }

    /**
     * 删除任务 — 仅平台管理员（逻辑删除）
     */
    @Operation(summary = "删除任务")
    @ApiAccessLog
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    public R<Void> deleteTask(@PathVariable("id") Long id) {
        techReformTaskService.removeById(id);
        return R.ok();
    }
}
