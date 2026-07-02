package com.icbc.sh.techmg.business.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.icbc.sh.techmg.business.dto.TechReformSubtaskCreateDTO;
import com.icbc.sh.techmg.business.dto.TechReformSubtaskUpdateDTO;
import com.icbc.sh.techmg.business.entity.TechReformSubtask;
import com.icbc.sh.techmg.business.service.TechReformSubtaskService;
import com.icbc.sh.techmg.business.vo.TechReformSubtaskVO;
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
 * 技改子任务 Controller
 */
@Slf4j
@Tag(name = "技改子任务管理")
@RestController
@RequestMapping("/api/tech-reform/subtask")
@RequiredArgsConstructor
public class TechReformSubtaskController {

    private final TechReformSubtaskService techReformSubtaskService;

    /**
     * 分页查询子任务列表
     */
    @Operation(summary = "分页查询子任务列表")
    @ApiAccessLog
    @GetMapping("/list")
    public R<PageResult<TechReformSubtaskVO>> list(@RequestParam(name = "page", defaultValue = "1") Integer page,
                                              @RequestParam(name = "size", defaultValue = "10") Integer size,
                                              @RequestParam(name = "parentTaskId", required = false) Long parentTaskId,
                                              @RequestParam(name = "status", required = false) String status,
                                              @RequestParam(name = "keyword", required = false) String keyword) {
        Page<TechReformSubtask> pageReq = new Page<>(page, size);
        return R.ok(PageResult.of(techReformSubtaskService.pageSubtasks(pageReq, parentTaskId, status, keyword)));
    }

    /**
     * 查询子任务详情
     */
    @Operation(summary = "查询子任务详情")
    @ApiAccessLog
    @GetMapping("/{id}")
    public R<TechReformSubtaskVO> getSubtask(@PathVariable("id") Long id) {
        return R.ok(techReformSubtaskService.getSubtaskVO(id));
    }

    /**
     * 创建子任务 — 平台管理员或部门管理员
     */
    @Operation(summary = "创建子任务")
    @ApiAccessLog
    @PostMapping
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'DEPT_ADMIN')")
    public R<TechReformSubtaskVO> createSubtask(@Valid @RequestBody TechReformSubtaskCreateDTO dto) {
        return R.ok(techReformSubtaskService.saveSubtask(dto));
    }

    /**
     * 更新子任务 — 平台管理员或部门管理员
     */
    @Operation(summary = "更新子任务")
    @ApiAccessLog
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'DEPT_ADMIN')")
    public R<TechReformSubtaskVO> updateSubtask(@PathVariable("id") Long id, @Valid @RequestBody TechReformSubtaskUpdateDTO dto) {
        dto.setId(id);
        return R.ok(techReformSubtaskService.updateSubtask(dto));
    }

    /**
     * 更新子任务状态 — 平台管理员或部门管理员
     */
    @Operation(summary = "更新子任务状态")
    @ApiAccessLog
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'DEPT_ADMIN')")
    public R<Void> updateStatus(@PathVariable("id") Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        techReformSubtaskService.updateStatus(id, status);
        return R.ok();
    }

    /**
     * 删除子任务 — 仅平台管理员
     */
    @Operation(summary = "删除子任务")
    @ApiAccessLog
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    public R<Void> deleteSubtask(@PathVariable("id") Long id) {
        techReformSubtaskService.removeById(id);
        return R.ok();
    }
}
