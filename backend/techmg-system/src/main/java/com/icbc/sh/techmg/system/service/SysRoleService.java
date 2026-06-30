package com.icbc.sh.techmg.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.icbc.sh.techmg.system.entity.SysRole;

import java.util.List;

public interface SysRoleService extends IService<SysRole> {
    List<SysRole> getRolesByUserId(Long userId);
    List<Long> getMenuIdsByRoleId(Long roleId);
    void assignMenus(Long roleId, List<Long> menuIds);
}
