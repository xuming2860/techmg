package com.icbc.sh.techmg.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.icbc.sh.techmg.common.model.R;
import com.icbc.sh.techmg.common.util.PageResult;
import com.icbc.sh.techmg.framework.log.ApiAccessLog;
import com.icbc.sh.techmg.system.dto.SysRoleCreateDTO;
import com.icbc.sh.techmg.system.dto.SysRoleUpdateDTO;
import com.icbc.sh.techmg.system.entity.SysRole;
import com.icbc.sh.techmg.system.service.SysMenuService;
import com.icbc.sh.techmg.system.service.SysRoleService;
import com.icbc.sh.techmg.system.vo.SysMenuVO;
import com.icbc.sh.techmg.system.vo.SysRoleVO;
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
    public R<PageResult<SysRoleVO>> list(@RequestParam(name = "page", defaultValue = "1") Integer page,
                                    @RequestParam(name = "size", defaultValue = "10") Integer size) {
        Page<SysRole> p = new Page<>(page, size);
        return R.ok(sysRoleService.pageRoles(p));
    }

    @Operation(summary = "根据ID查询角色")
    @GetMapping("/{id}")
    public R<SysRoleVO> getById(@PathVariable("id") Long id) {
        return R.ok(sysRoleService.getRoleVO(id));
    }

    @Operation(summary = "新增角色")
    @ApiAccessLog
    @PostMapping
    public R<SysRoleVO> create(@Valid @RequestBody SysRoleCreateDTO dto) {
        return R.ok(sysRoleService.saveRole(dto));
    }

    @Operation(summary = "更新角色")
    @ApiAccessLog
    @PutMapping
    public R<SysRoleVO> update(@Valid @RequestBody SysRoleUpdateDTO dto) {
        return R.ok(sysRoleService.updateRole(dto));
    }

    @Operation(summary = "删除角色")
    @ApiAccessLog
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable("id") Long id) {
        SysRoleVO role = sysRoleService.getRoleVO(id);
        if (role == null) {
            return R.ok();
        }
        sysRoleService.removeById(id);
        return R.ok();
    }

    @Operation(summary = "获取角色的菜单列表")
    @GetMapping("/{roleId}/menus")
    public R<List<SysMenuVO>> getRoleMenus(@PathVariable("roleId") Long roleId) {
        return R.ok(sysMenuService.getMenusByRoleId(roleId));
    }

    @Operation(summary = "分配角色菜单")
    @ApiAccessLog
    @PostMapping("/{roleId}/menus")
    public R<Void> assignMenus(@PathVariable("roleId") Long roleId, @RequestBody List<Long> menuIds) {
        SysRoleVO role = sysRoleService.getRoleVO(roleId);
        if (role == null) {
            return R.ok();
        }
        sysRoleService.assignMenus(roleId, menuIds);
        return R.ok();
    }
}
