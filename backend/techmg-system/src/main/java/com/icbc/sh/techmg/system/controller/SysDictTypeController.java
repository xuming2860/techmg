package com.icbc.sh.techmg.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.icbc.sh.techmg.common.model.R;
import com.icbc.sh.techmg.common.util.PageResult;
import com.icbc.sh.techmg.framework.log.ApiAccessLog;
import com.icbc.sh.techmg.system.dto.SysDictTypeCreateDTO;
import com.icbc.sh.techmg.system.dto.SysDictTypeUpdateDTO;
import com.icbc.sh.techmg.system.entity.SysDictType;
import com.icbc.sh.techmg.system.service.SysDictTypeService;
import com.icbc.sh.techmg.system.vo.SysDictTypeVO;
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
    public R<PageResult<SysDictTypeVO>> list(@RequestParam(name = "page", defaultValue = "1") int page,
                                       @RequestParam(name = "size", defaultValue = "10") int size) {
        Page<SysDictType> p = new Page<>(page, size);
        return R.ok(sysDictTypeService.pageDictTypes(p));
    }

    @Operation(summary = "根据ID查询字典类型")
    @GetMapping("/{id}")
    public R<SysDictTypeVO> getById(@PathVariable("id") Long id) {
        return R.ok(sysDictTypeService.getDictTypeVO(id));
    }

    @Operation(summary = "新增字典类型")
    @ApiAccessLog
    @PostMapping
    public R<SysDictTypeVO> create(@Valid @RequestBody SysDictTypeCreateDTO dto) {
        return R.ok(sysDictTypeService.saveDictType(dto));
    }

    @Operation(summary = "更新字典类型")
    @ApiAccessLog
    @PutMapping
    public R<SysDictTypeVO> update(@Valid @RequestBody SysDictTypeUpdateDTO dto) {
        return R.ok(sysDictTypeService.updateDictType(dto));
    }

    @Operation(summary = "删除字典类型")
    @ApiAccessLog
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable("id") Long id) {
        sysDictTypeService.deleteDictType(id);
        return R.ok();
    }
}
