package com.icbc.sh.techmg.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.icbc.sh.techmg.common.constant.ResultCode;
import com.icbc.sh.techmg.common.exception.BusinessException;
import com.icbc.sh.techmg.common.model.R;
import com.icbc.sh.techmg.framework.log.ApiAccessLog;
import com.icbc.sh.techmg.system.entity.SysRole;
import com.icbc.sh.techmg.system.entity.SysUser;
import com.icbc.sh.techmg.system.model.dto.UserCreateDTO;
import com.icbc.sh.techmg.system.model.dto.UserQueryDTO;
import com.icbc.sh.techmg.system.model.dto.UserRoleDTO;
import com.icbc.sh.techmg.system.model.dto.UserUpdateDTO;
import com.icbc.sh.techmg.system.service.SysRoleService;
import com.icbc.sh.techmg.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/api/system/user")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService sysUserService;
    private final SysRoleService sysRoleService;

    @Operation(summary = "分页查询用户列表")
    @GetMapping("/list")
    public R<IPage<SysUser>> list(@RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "10") Integer size,
                                   @RequestParam(required = false) String keyword) {
        UserQueryDTO dto = new UserQueryDTO();
        dto.setPage(page);
        dto.setSize(size);
        dto.setKeyword(keyword);
        return R.ok(sysUserService.pageUsers(dto));
    }

    @Operation(summary = "根据ID查询用户")
    @GetMapping("/{id}")
    public R<SysUser> getById(@PathVariable("id") Long id) {
        SysUser user = sysUserService.getById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }
        return R.ok(user);
    }

    @Operation(summary = "新增用户")
    @ApiAccessLog
    @PostMapping
    public R<SysUser> create(@Valid @RequestBody UserCreateDTO dto) {
        SysUser user = new SysUser();
        user.setAuthNo(dto.getAuthNo());
        user.setUsername(dto.getUsername());
        user.setRealName(dto.getRealName());
        user.setDeptId(dto.getDeptId());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setStatus(1);
        sysUserService.save(user);
        return R.ok(user);
    }

    @Operation(summary = "更新用户")
    @ApiAccessLog
    @PutMapping
    public R<SysUser> update(@Valid @RequestBody UserUpdateDTO dto) {
        SysUser user = sysUserService.getById(dto.getId());
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }
        if (dto.getRealName() != null) user.setRealName(dto.getRealName());
        if (dto.getDeptId() != null) user.setDeptId(dto.getDeptId());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        if (dto.getStatus() != null) user.setStatus(dto.getStatus());
        sysUserService.updateById(user);
        return R.ok(user);
    }

    @Operation(summary = "删除用户（逻辑删除）")
    @ApiAccessLog
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable("id") Long id) {
        SysUser user = sysUserService.getById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }
        sysUserService.removeById(id);
        return R.ok();
    }

    @Operation(summary = "分配用户角色")
    @ApiAccessLog
    @PostMapping("/{userId}/roles")
    public R<Void> assignRoles(@PathVariable("userId") Long userId, @RequestBody UserRoleDTO dto) {
        SysUser user = sysUserService.getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }
        sysUserService.assignRoles(userId, dto.getRoleIds());
        return R.ok();
    }

    @Operation(summary = "获取用户角色")
    @GetMapping("/{userId}/roles")
    public R<List<SysRole>> getUserRoles(@PathVariable("userId") Long userId) {
        return R.ok(sysRoleService.getRolesByUserId(userId));
    }
}
