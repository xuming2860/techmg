package com.icbc.sh.techmg.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.icbc.sh.techmg.system.entity.SysRole;
import com.icbc.sh.techmg.system.entity.SysRoleMenu;
import com.icbc.sh.techmg.system.entity.SysUserRole;
import com.icbc.sh.techmg.system.mapper.SysRoleMapper;
import com.icbc.sh.techmg.system.mapper.SysRoleMenuMapper;
import com.icbc.sh.techmg.system.mapper.SysUserRoleMapper;
import com.icbc.sh.techmg.system.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRoleMenuMapper sysRoleMenuMapper;

    @Override
    public List<SysRole> getRolesByUserId(Long userId) {
        LambdaQueryWrapper<SysUserRole> urWrapper = new LambdaQueryWrapper<>();
        urWrapper.eq(SysUserRole::getUserId, userId);
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(urWrapper);

        if (userRoles.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> roleIds = userRoles.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());
        return this.listByIds(roleIds);
    }

    @Override
    public List<Long> getMenuIdsByRoleId(Long roleId) {
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId, roleId);
        List<SysRoleMenu> roleMenus = sysRoleMenuMapper.selectList(wrapper);
        return roleMenus.stream()
                .map(SysRoleMenu::getMenuId)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignMenus(Long roleId, List<Long> menuIds) {
        // clear existing
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId, roleId);
        sysRoleMenuMapper.delete(wrapper);

        // insert new
        if (menuIds != null && !menuIds.isEmpty()) {
            for (Long menuId : menuIds) {
                SysRoleMenu rm = new SysRoleMenu();
                rm.setRoleId(roleId);
                rm.setMenuId(menuId);
                sysRoleMenuMapper.insert(rm);
            }
        }
    }
}
