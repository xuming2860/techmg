package com.icbc.sh.techmg.system.controller;

import com.icbc.sh.techmg.common.model.R;
import com.icbc.sh.techmg.framework.log.ApiAccessLog;
import com.icbc.sh.techmg.system.dto.SysDictDataCreateDTO;
import com.icbc.sh.techmg.system.dto.SysDictDataUpdateDTO;
import com.icbc.sh.techmg.system.service.SysDictDataService;
import com.icbc.sh.techmg.system.vo.SysDictDataVO;
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
    public R<List<SysDictDataVO>> getByDictType(@PathVariable("dictType") String dictType) {
        return R.ok(sysDictDataService.getByDictType(dictType));
    }

    @Operation(summary = "根据ID查询字典数据")
    @GetMapping("/detail/{id}")
    public R<SysDictDataVO> getById(@PathVariable("id") Long id) {
        return R.ok(sysDictDataService.getDictDataVO(id));
    }

    @Operation(summary = "新增字典数据")
    @ApiAccessLog
    @PostMapping
    public R<SysDictDataVO> create(@Valid @RequestBody SysDictDataCreateDTO dto) {
        return R.ok(sysDictDataService.saveDictData(dto));
    }

    @Operation(summary = "更新字典数据")
    @ApiAccessLog
    @PutMapping
    public R<SysDictDataVO> update(@Valid @RequestBody SysDictDataUpdateDTO dto) {
        return R.ok(sysDictDataService.updateDictData(dto));
    }

    @Operation(summary = "删除字典数据")
    @ApiAccessLog
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable("id") Long id) {
        sysDictDataService.deleteDictData(id);
        return R.ok();
    }
}
