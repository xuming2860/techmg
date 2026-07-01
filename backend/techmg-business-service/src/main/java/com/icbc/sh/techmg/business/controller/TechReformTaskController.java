package com.icbc.sh.techmg.business.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.icbc.sh.techmg.business.entity.TechReformTask;
import com.icbc.sh.techmg.business.service.TechReformTaskService;
import com.icbc.sh.techmg.common.constant.ResultCode;
import com.icbc.sh.techmg.common.model.R;
import com.icbc.sh.techmg.framework.log.ApiAccessLog;
import com.icbc.sh.techmg.system.entity.SysUser;
import com.icbc.sh.techmg.system.service.SysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 技改任务 Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/tech-reform/task")
@RequiredArgsConstructor
public class TechReformTaskController {

    private final TechReformTaskService techReformTaskService;
    private final SysUserService sysUserService;

    /**
     * 分页查询任务列表
     */
    @ApiAccessLog
    @GetMapping("/list")
    public R<IPage<TechReformTask>> list(@RequestParam(name = "page", defaultValue = "1") Integer page,
                                          @RequestParam(name = "size", defaultValue = "10") Integer size,
                                          @RequestParam(name = "keyword", required = false) String keyword,
                                          @RequestParam(name = "category", required = false) String category,
                                          @RequestParam(name = "status", required = false) String status) {
        Page<TechReformTask> pageReq = new Page<>(page, size);
        return R.ok(techReformTaskService.pageTasks(pageReq, keyword, category, status));
    }

    /**
     * 查询任务详情
     */
    @ApiAccessLog
    @GetMapping("/{id}")
    public R<TechReformTask> getTask(@PathVariable("id") Long id) {
        TechReformTask task = techReformTaskService.getById(id);
        if (task == null) {
            return R.fail(ResultCode.NOT_FOUND, "任务不存在");
        }
        return R.ok(task);
    }

    /**
     * 创建任务 — 仅平台管理员
     */
    @ApiAccessLog
    @PostMapping
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    public R<TechReformTask> createTask(@RequestBody TechReformTask task) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof UserDetails userDetails) {
            String authNo = userDetails.getUsername();
            SysUser sysUser = sysUserService.getByAuthNo(authNo);
            if (sysUser != null) {
                task.setTaskOwner(sysUser.getRealName() != null ? sysUser.getRealName() : authNo);
            } else {
                task.setTaskOwner(authNo);
            }
        }
        techReformTaskService.save(task);
        return R.ok(task);
    }

    /**
     * 更新任务 — 仅平台管理员
     */
    @ApiAccessLog
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    public R<TechReformTask> updateTask(@PathVariable Long id, @RequestBody TechReformTask task) {
        TechReformTask existing = techReformTaskService.getById(id);
        if (existing == null) {
            return R.fail(ResultCode.NOT_FOUND, "任务不存在");
        }
        task.setId(id);
        techReformTaskService.updateById(task);
        return R.ok(task);
    }

    /**
     * 更新任务状态 — 仅平台管理员
     */
    @ApiAccessLog
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    public R<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        TechReformTask existing = techReformTaskService.getById(id);
        if (existing == null) {
            return R.fail(ResultCode.NOT_FOUND, "任务不存在");
        }
        String status = body.get("status");
        if (status == null || status.isBlank()) {
            return R.fail(ResultCode.PARAM_ERROR, "状态值不能为空");
        }
        techReformTaskService.updateStatus(id, status);
        return R.ok();
    }

    /**
     * 删除任务 — 仅平台管理员（逻辑删除）
     */
    @ApiAccessLog
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    public R<Void> deleteTask(@PathVariable Long id) {
        TechReformTask existing = techReformTaskService.getById(id);
        if (existing == null) {
            return R.fail(ResultCode.NOT_FOUND, "任务不存在");
        }
        techReformTaskService.removeById(id);
        return R.ok();
    }
}
