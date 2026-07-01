package com.icbc.sh.techmg.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.icbc.sh.techmg.common.constant.ResultCode;
import com.icbc.sh.techmg.common.exception.BusinessException;
import com.icbc.sh.techmg.common.model.R;
import com.icbc.sh.techmg.framework.log.ApiAccessLog;
import com.icbc.sh.techmg.system.entity.SysDept;
import com.icbc.sh.techmg.system.service.SysDeptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "部门管理")
@RestController
@RequestMapping("/api/system/dept")
@RequiredArgsConstructor
public class SysDeptController {

    private final SysDeptService sysDeptService;

    @Operation(summary = "获取部门树")
    @GetMapping("/tree")
    public R<List<SysDept>> tree() {
        return R.ok(sysDeptService.getDeptTree());
    }

    @Operation(summary = "根据ID查询部门")
    @GetMapping("/{id}")
    public R<SysDept> getById(@PathVariable("id") Long id) {
        SysDept dept = sysDeptService.getById(id);
        if (dept == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "部门不存在");
        }
        return R.ok(dept);
    }

    @Operation(summary = "新增部门")
    @ApiAccessLog
    @PostMapping
    public R<SysDept> create(@Valid @RequestBody SysDept dept) {
        sysDeptService.save(dept);
        return R.ok(dept);
    }

    @Operation(summary = "更新部门")
    @ApiAccessLog
    @PutMapping
    public R<SysDept> update(@Valid @RequestBody SysDept dept) {
        SysDept existing = sysDeptService.getById(dept.getId());
        if (existing == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "部门不存在");
        }
        sysDeptService.updateById(dept);
        return R.ok(dept);
    }

    @Operation(summary = "删除部门（检查子部门）")
    @ApiAccessLog
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable("id") Long id) {
        SysDept dept = sysDeptService.getById(id);
        if (dept == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "部门不存在");
        }
        // check children
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDept::getParentId, id);
        long childCount = sysDeptService.count(wrapper);
        if (childCount > 0) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "存在子部门，无法删除");
        }
        sysDeptService.removeById(id);
        return R.ok();
    }
}
