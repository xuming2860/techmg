package com.icbc.sh.techmg.system.controller;

import com.icbc.sh.techmg.common.model.R;
import com.icbc.sh.techmg.framework.log.ApiAccessLog;
import com.icbc.sh.techmg.system.dto.SysMenuCreateDTO;
import com.icbc.sh.techmg.system.dto.SysMenuUpdateDTO;
import com.icbc.sh.techmg.system.entity.SysUser;
import com.icbc.sh.techmg.system.service.SysMenuService;
import com.icbc.sh.techmg.system.service.SysUserService;
import com.icbc.sh.techmg.system.vo.SysMenuVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "菜单管理")
@RestController
@RequestMapping("/api/system/menu")
@RequiredArgsConstructor
public class SysMenuController {

    private final SysMenuService sysMenuService;
    private final SysUserService sysUserService;

    @Operation(summary = "获取菜单树")
    @GetMapping("/tree")
    public R<List<SysMenuVO>> tree() {
        return R.ok(sysMenuService.getMenuTree());
    }

    @Operation(summary = "获取当前用户菜单树（按角色过滤）")
    @GetMapping("/user-tree")
    public R<List<SysMenuVO>> userTree() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return R.fail(401, "未登录");
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String authNo = userDetails.getUsername();
        SysUser sysUser = sysUserService.getByAuthNo(authNo);
        if (sysUser == null) {
            return R.fail(401, "用户不存在");
        }
        return R.ok(sysMenuService.getMenuTreeByUserId(sysUser.getId()));
    }

    @Operation(summary = "根据ID查询菜单")
    @GetMapping("/{id}")
    public R<SysMenuVO> getById(@PathVariable("id") Long id) {
        return R.ok(sysMenuService.getMenuVO(id));
    }

    @Operation(summary = "新增菜单")
    @ApiAccessLog
    @PostMapping
    public R<SysMenuVO> create(@Valid @RequestBody SysMenuCreateDTO dto) {
        return R.ok(sysMenuService.saveMenu(dto));
    }

    @Operation(summary = "更新菜单")
    @ApiAccessLog
    @PutMapping
    public R<SysMenuVO> update(@Valid @RequestBody SysMenuUpdateDTO dto) {
        return R.ok(sysMenuService.updateMenu(dto));
    }

    @Operation(summary = "删除菜单（含子菜单校验）")
    @ApiAccessLog
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable("id") Long id) {
        sysMenuService.deleteMenu(id);
        return R.ok();
    }
}
