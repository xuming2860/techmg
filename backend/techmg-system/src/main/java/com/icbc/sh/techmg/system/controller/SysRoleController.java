package com.icbc.sh.techmg.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.icbc.sh.techmg.common.constant.ResultCode;
import com.icbc.sh.techmg.common.exception.BusinessException;
import com.icbc.sh.techmg.common.model.R;
import com.icbc.sh.techmg.framework.log.ApiAccessLog;
import com.icbc.sh.techmg.system.entity.SysMenu;
import com.icbc.sh.techmg.system.entity.SysRole;
import com.icbc.sh.techmg.system.service.SysMenuService;
import com.icbc.sh.techmg.system.service.SysRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "角色管理")
@RestController
@RequestMapping("/api/system/role")
@RequiredArgsConstructor
public class SysRoleController {

    private final SysRoleService sysRoleService;
    private final SysMenuService sysMenuService;

    @Operation(summary = "分页查询角色列表")
    @GetMapping("/list")
    public R<IPage<SysRole>> list(@RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "10") Integer size) {
        Page<SysRole> p = new Page<>(page, size);
        return R.ok(sysRoleService.page(p));
    }

    @Operation(summary = "根据ID查询角色")
    @GetMapping("/{id}")
    public R<SysRole> getById(@PathVariable Long id) {
        SysRole role = sysRoleService.getById(id);
        if (role == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "角色不存在");
        }
        return R.ok(role);
    }

    @Operation(summary = "新增角色")
    @ApiAccessLog
    @PostMapping
    public R<SysRole> create(@Valid @RequestBody SysRole role) {
        sysRoleService.save(role);
        return R.ok(role);
    }

    @Operation(summary = "更新角色")
    @ApiAccessLog
    @PutMapping
    public R<SysRole> update(@Valid @RequestBody SysRole role) {
        SysRole existing = sysRoleService.getById(role.getId());
        if (existing == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "角色不存在");
        }
        sysRoleService.updateById(role);
        return R.ok(role);
    }

    @Operation(summary = "删除角色")
    @ApiAccessLog
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        SysRole role = sysRoleService.getById(id);
        if (role == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "角色不存在");
        }
        sysRoleService.removeById(id);
        return R.ok();
    }

    @Operation(summary = "获取角色的菜单列表")
    @GetMapping("/{roleId}/menus")
    public R<List<SysMenu>> getRoleMenus(@PathVariable Long roleId) {
        return R.ok(sysMenuService.getMenusByRoleId(roleId));
    }

    @Operation(summary = "分配角色菜单")
    @ApiAccessLog
    @PostMapping("/{roleId}/menus")
    public R<Void> assignMenus(@PathVariable Long roleId, @RequestBody List<Long> menuIds) {
        SysRole role = sysRoleService.getById(roleId);
        if (role == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "角色不存在");
        }
        sysRoleService.assignMenus(roleId, menuIds);
        return R.ok();
    }
}
