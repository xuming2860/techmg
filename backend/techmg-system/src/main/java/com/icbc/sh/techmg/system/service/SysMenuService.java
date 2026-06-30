package com.icbc.sh.techmg.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.icbc.sh.techmg.system.entity.SysMenu;

import java.util.List;

public interface SysMenuService extends IService<SysMenu> {
    List<SysMenu> getMenuTree();
    List<SysMenu> getMenusByRoleId(Long roleId);
    List<SysMenu> getMenuTreeByUserId(Long userId);
}
