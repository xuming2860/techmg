package com.icbc.sh.techmg.system.controller;

import com.icbc.sh.techmg.common.model.R;
import com.icbc.sh.techmg.framework.log.ApiAccessLog;
import com.icbc.sh.techmg.system.dto.SysDeptCreateDTO;
import com.icbc.sh.techmg.system.dto.SysDeptUpdateDTO;
import com.icbc.sh.techmg.system.service.SysDeptService;
import com.icbc.sh.techmg.system.vo.SysDeptVO;
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
    public R<List<SysDeptVO>> tree() {
        return R.ok(sysDeptService.getDeptTree());
    }

    @Operation(summary = "根据ID查询部门")
    @GetMapping("/{id}")
    public R<SysDeptVO> getById(@PathVariable("id") Long id) {
        return R.ok(sysDeptService.getDeptVO(id));
    }

    @Operation(summary = "新增部门")
    @ApiAccessLog
    @PostMapping
    public R<SysDeptVO> create(@Valid @RequestBody SysDeptCreateDTO dto) {
        return R.ok(sysDeptService.saveDept(dto));
    }

    @Operation(summary = "更新部门")
    @ApiAccessLog
    @PutMapping
    public R<SysDeptVO> update(@Valid @RequestBody SysDeptUpdateDTO dto) {
        return R.ok(sysDeptService.updateDept(dto));
    }

    @Operation(summary = "删除部门（含子部门校验）")
    @ApiAccessLog
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable("id") Long id) {
        sysDeptService.deleteDept(id);
        return R.ok();
    }
}
