package com.icbc.sh.techmg.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.icbc.sh.techmg.common.constant.ResultCode;
import com.icbc.sh.techmg.common.exception.BusinessException;
import com.icbc.sh.techmg.common.model.R;
import com.icbc.sh.techmg.framework.log.ApiAccessLog;
import com.icbc.sh.techmg.system.entity.SysMenu;
import com.icbc.sh.techmg.system.entity.SysUser;
import com.icbc.sh.techmg.system.service.SysMenuService;
import com.icbc.sh.techmg.system.service.SysUserService;
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
    public R<List<SysMenu>> tree() {
        return R.ok(sysMenuService.getMenuTree());
    }

    @Operation(summary = "获取当前用户菜单树（按角色过滤）")
    @GetMapping("/user-tree")
    public R<List<SysMenu>> userTree() {
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
    public R<SysMenu> getById(@PathVariable Long id) {
        SysMenu menu = sysMenuService.getById(id);
        if (menu == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "菜单不存在");
        }
        return R.ok(menu);
    }

    @Operation(summary = "新增菜单")
    @ApiAccessLog
    @PostMapping
    public R<SysMenu> create(@Valid @RequestBody SysMenu menu) {
        sysMenuService.save(menu);
        return R.ok(menu);
    }

    @Operation(summary = "更新菜单")
    @ApiAccessLog
    @PutMapping
    public R<SysMenu> update(@Valid @RequestBody SysMenu menu) {
        SysMenu existing = sysMenuService.getById(menu.getId());
        if (existing == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "菜单不存在");
        }
        sysMenuService.updateById(menu);
        return R.ok(menu);
    }

    @Operation(summary = "删除菜单（检查子菜单）")
    @ApiAccessLog
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        SysMenu menu = sysMenuService.getById(id);
        if (menu == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "菜单不存在");
        }
        // check children
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getParentId, id);
        long childCount = sysMenuService.count(wrapper);
        if (childCount > 0) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "存在子菜单，无法删除");
        }
        sysMenuService.removeById(id);
        return R.ok();
    }
}
