package com.icbc.sh.techmg.system.controller;

import com.icbc.sh.techmg.common.constant.ResultCode;
import com.icbc.sh.techmg.common.exception.BusinessException;
import com.icbc.sh.techmg.common.model.R;
import com.icbc.sh.techmg.framework.log.ApiAccessLog;
import com.icbc.sh.techmg.system.entity.SysDictData;
import com.icbc.sh.techmg.system.service.SysDictDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "字典数据管理")
@RestController
@RequestMapping("/api/system/dict/data")
@RequiredArgsConstructor
public class SysDictDataController {

    private final SysDictDataService sysDictDataService;

    @Operation(summary = "根据字典类型获取字典数据")
    @GetMapping("/{dictType}")
    public R<List<SysDictData>> getByDictType(@PathVariable String dictType) {
        return R.ok(sysDictDataService.getByDictType(dictType));
    }

    @Operation(summary = "根据ID查询字典数据")
    @GetMapping("/detail/{id}")
    public R<SysDictData> getById(@PathVariable Long id) {
        SysDictData dictData = sysDictDataService.getById(id);
        if (dictData == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "字典数据不存在");
        }
        return R.ok(dictData);
    }

    @Operation(summary = "新增字典数据")
    @ApiAccessLog
    @PostMapping
    public R<SysDictData> create(@Valid @RequestBody SysDictData dictData) {
        sysDictDataService.save(dictData);
        return R.ok(dictData);
    }

    @Operation(summary = "更新字典数据")
    @ApiAccessLog
    @PutMapping
    public R<SysDictData> update(@Valid @RequestBody SysDictData dictData) {
        SysDictData existing = sysDictDataService.getById(dictData.getId());
        if (existing == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "字典数据不存在");
        }
        sysDictDataService.updateById(dictData);
        return R.ok(dictData);
    }

    @Operation(summary = "删除字典数据")
    @ApiAccessLog
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        SysDictData dictData = sysDictDataService.getById(id);
        if (dictData == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "字典数据不存在");
        }
        sysDictDataService.removeById(id);
        return R.ok();
    }
}
