package com.icbc.sh.techmg.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.icbc.sh.techmg.common.constant.ResultCode;
import com.icbc.sh.techmg.common.exception.BusinessException;
import com.icbc.sh.techmg.common.model.R;
import com.icbc.sh.techmg.framework.log.ApiAccessLog;
import com.icbc.sh.techmg.system.entity.SysDictType;
import com.icbc.sh.techmg.system.service.SysDictTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "字典类型管理")
@RestController
@RequestMapping("/api/system/dict/type")
@RequiredArgsConstructor
public class SysDictTypeController {

    private final SysDictTypeService sysDictTypeService;

    @Operation(summary = "分页查询字典类型")
    @GetMapping("/list")
    public R<IPage<SysDictType>> list(@RequestParam(name = "page", defaultValue = "1") int page,
                                       @RequestParam(name = "size", defaultValue = "10") int size) {
        Page<SysDictType> p = new Page<>(page, size);
        return R.ok(sysDictTypeService.page(p));
    }

    @Operation(summary = "根据ID查询字典类型")
    @GetMapping("/{id}")
    public R<SysDictType> getById(@PathVariable("id") Long id) {
        SysDictType dictType = sysDictTypeService.getById(id);
        if (dictType == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "字典类型不存在");
        }
        return R.ok(dictType);
    }

    @Operation(summary = "新增字典类型")
    @ApiAccessLog
    @PostMapping
    public R<SysDictType> create(@Valid @RequestBody SysDictType dictType) {
        // check uniqueness
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictType::getDictType, dictType.getDictType());
        if (sysDictTypeService.count(wrapper) > 0) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "字典类型已存在");
        }
        sysDictTypeService.save(dictType);
        return R.ok(dictType);
    }

    @Operation(summary = "更新字典类型")
    @ApiAccessLog
    @PutMapping
    public R<SysDictType> update(@Valid @RequestBody SysDictType dictType) {
        SysDictType existing = sysDictTypeService.getById(dictType.getId());
        if (existing == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "字典类型不存在");
        }
        sysDictTypeService.updateById(dictType);
        return R.ok(dictType);
    }

    @Operation(summary = "删除字典类型")
    @ApiAccessLog
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable("id") Long id) {
        SysDictType dictType = sysDictTypeService.getById(id);
        if (dictType == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "字典类型不存在");
        }
        sysDictTypeService.removeById(id);
        return R.ok();
    }
}
