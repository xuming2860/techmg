package com.icbc.sh.techmg.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.icbc.sh.techmg.system.entity.SysMenu;
import com.icbc.sh.techmg.system.entity.SysRole;
import com.icbc.sh.techmg.system.entity.SysRoleMenu;
import com.icbc.sh.techmg.system.entity.SysUserRole;
import com.icbc.sh.techmg.system.mapper.SysMenuMapper;
import com.icbc.sh.techmg.system.mapper.SysRoleMapper;
import com.icbc.sh.techmg.system.mapper.SysRoleMenuMapper;
import com.icbc.sh.techmg.system.mapper.SysUserRoleMapper;
import com.icbc.sh.techmg.system.service.SysMenuService;
import com.icbc.sh.techmg.common.util.TreeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    private final SysRoleMenuMapper sysRoleMenuMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRoleMapper sysRoleMapper;

    @Override
    public List<SysMenu> getMenuTree() {
        List<SysMenu> allMenus = this.list();
        if (allMenus.isEmpty()) {
            return Collections.emptyList();
        }
        allMenus.sort(Comparator.comparing(SysMenu::getSort, Comparator.nullsLast(Comparator.naturalOrder())));
        setLayout(allMenus);
        return TreeUtil.buildTree(allMenus, SysMenu::getId, SysMenu::getParentId,
                SysMenu::setChildren, 0L);
    }

    @Override
    public List<SysMenu> getMenusByRoleId(Long roleId) {
        LambdaQueryWrapper<SysRoleMenu> rmWrapper = new LambdaQueryWrapper<>();
        rmWrapper.eq(SysRoleMenu::getRoleId, roleId);
        List<SysRoleMenu> roleMenus = sysRoleMenuMapper.selectList(rmWrapper);

        if (roleMenus.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> menuIds = roleMenus.stream()
                .map(SysRoleMenu::getMenuId)
                .collect(Collectors.toList());
        return this.listByIds(menuIds);
    }

    @Override
    public List<SysMenu> getMenuTreeByUserId(Long userId) {
        // 1. get user's role IDs
        LambdaQueryWrapper<SysUserRole> urWrapper = new LambdaQueryWrapper<>();
        urWrapper.eq(SysUserRole::getUserId, userId);
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(urWrapper);

        if (userRoles.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> roleIds = userRoles.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());

        // 2. check if user is PLATFORM_ADMIN — return ALL menus
        List<SysRole> roles = sysRoleMapper.selectBatchIds(roleIds);
        boolean isPlatformAdmin = roles.stream()
                .anyMatch(r -> "PLATFORM_ADMIN".equals(r.getRoleCode()));
        if (isPlatformAdmin) {
            return getMenuTree();
        }

        // 3. collect all accessible menu IDs from all roles
        LambdaQueryWrapper<SysRoleMenu> rmWrapper = new LambdaQueryWrapper<>();
        rmWrapper.in(SysRoleMenu::getRoleId, roleIds);
        List<SysRoleMenu> roleMenus = sysRoleMenuMapper.selectList(rmWrapper);

        if (roleMenus.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> accessibleMenuIds = roleMenus.stream()
                .map(SysRoleMenu::getMenuId)
                .collect(Collectors.toSet());

        // 4. filter: only keep menus whose id is in the accessible set
        List<SysMenu> allMenus = this.list();
        List<SysMenu> filtered = allMenus.stream()
                .filter(m -> accessibleMenuIds.contains(m.getId()))
                .sorted(Comparator.comparing(SysMenu::getSort, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());

        // 5. build tree from filtered list; parent directories not in the set won't appear,
        //    so ensure parent directories are included even if only children were assigned
        Set<Long> menuIdSet = filtered.stream().map(SysMenu::getId).collect(Collectors.toSet());
        for (SysMenu menu : allMenus) {
            if (menu.getParentId() != null && menu.getParentId() > 0
                    && menuIdSet.contains(menu.getId())
                    && !menuIdSet.contains(menu.getParentId())) {
                SysMenu parent = allMenus.stream()
                        .filter(m -> m.getId().equals(menu.getParentId()))
                        .findFirst().orElse(null);
                if (parent != null && !menuIdSet.contains(parent.getId())) {
                    filtered.add(parent);
                    menuIdSet.add(parent.getId());
                }
            }
        }
        filtered.sort(Comparator.comparing(SysMenu::getSort, Comparator.nullsLast(Comparator.naturalOrder())));
        setLayout(filtered);

        return TreeUtil.buildTree(filtered, SysMenu::getId, SysMenu::getParentId,
                SysMenu::setChildren, 0L);
    }

    /**
     * Set layout on each menu:
     * - id=1 (首页/dashboard) → "top" (上下布局)
     * - all other menus → "side" (上左右布局)
     */
    private void setLayout(List<SysMenu> menus) {
        for (SysMenu menu : menus) {
            if (menu.getId() != null && menu.getId() == 1L) {
                menu.setLayout("top");
            } else {
                menu.setLayout("side");
            }
        }
    }
}
