package com.icbc.sh.techmg.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.icbc.sh.techmg.common.constant.ResultCode;
import com.icbc.sh.techmg.common.exception.BusinessException;
import com.icbc.sh.techmg.common.util.TreeUtil;
import com.icbc.sh.techmg.system.dto.SysMenuCreateDTO;
import com.icbc.sh.techmg.system.dto.SysMenuUpdateDTO;
import com.icbc.sh.techmg.system.entity.SysMenu;
import com.icbc.sh.techmg.system.entity.SysRole;
import com.icbc.sh.techmg.system.entity.SysRoleMenu;
import com.icbc.sh.techmg.system.entity.SysUserRole;
import com.icbc.sh.techmg.system.mapper.SysMenuMapper;
import com.icbc.sh.techmg.system.mapper.SysRoleMapper;
import com.icbc.sh.techmg.system.mapper.SysRoleMenuMapper;
import com.icbc.sh.techmg.system.mapper.SysUserRoleMapper;
import com.icbc.sh.techmg.system.service.SysMenuService;
import com.icbc.sh.techmg.system.vo.SysMenuVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    private final SysRoleMenuMapper sysRoleMenuMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRoleMapper sysRoleMapper;

    @Override
    public List<SysMenuVO> getMenuTree() {
        List<SysMenu> allMenus = this.list();
        if (allMenus.isEmpty()) {
            return Collections.emptyList();
        }
        allMenus.sort(Comparator.comparing(SysMenu::getSort, Comparator.nullsLast(Comparator.naturalOrder())));
        List<SysMenuVO> voList = allMenus.stream().map(this::toVO).collect(Collectors.toList());
        setLayout(voList);
        return TreeUtil.buildTree(voList, SysMenuVO::getId, SysMenuVO::getParentId,
                SysMenuVO::setChildren, 0L);
    }

    @Override
    public List<SysMenuVO> getMenusByRoleId(Long roleId) {
        LambdaQueryWrapper<SysRoleMenu> rmWrapper = new LambdaQueryWrapper<>();
        rmWrapper.eq(SysRoleMenu::getRoleId, roleId);
        List<SysRoleMenu> roleMenus = sysRoleMenuMapper.selectList(rmWrapper);

        if (roleMenus.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> menuIds = roleMenus.stream()
                .map(SysRoleMenu::getMenuId)
                .collect(Collectors.toList());
        List<SysMenu> menus = this.listByIds(menuIds);
        return menus.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public List<SysMenuVO> getMenuTreeByUserId(Long userId) {
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
                        .filter(m -> Objects.equals(m.getId(), menu.getParentId()))
                        .findFirst().orElse(null);
                if (parent != null && !menuIdSet.contains(parent.getId())) {
                    filtered.add(parent);
                    menuIdSet.add(parent.getId());
                }
            }
        }
        filtered.sort(Comparator.comparing(SysMenu::getSort, Comparator.nullsLast(Comparator.naturalOrder())));

        List<SysMenuVO> voList = filtered.stream().map(this::toVO).collect(Collectors.toList());
        setLayout(voList);

        return TreeUtil.buildTree(voList, SysMenuVO::getId, SysMenuVO::getParentId,
                SysMenuVO::setChildren, 0L);
    }

    @Override
    public SysMenuVO getMenuVO(Long id) {
        SysMenu entity = getById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "菜单不存在");
        }
        return toVO(entity);
    }

    @Override
    public SysMenuVO saveMenu(SysMenuCreateDTO dto) {
        SysMenu entity = new SysMenu();
        BeanUtils.copyProperties(dto, entity);
        save(entity);
        return toVO(entity);
    }

    @Override
    public SysMenuVO updateMenu(SysMenuUpdateDTO dto) {
        SysMenu existing = getById(dto.getId());
        if (existing == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "菜单不存在");
        }
        BeanUtils.copyProperties(dto, existing);
        updateById(existing);
        return toVO(existing);
    }

    @Override
    public void deleteMenu(Long id) {
        SysMenu menu = getById(id);
        if (menu == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "菜单不存在");
        }
        long childCount = count(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getParentId, id));
        if (childCount > 0) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "存在子菜单，无法删除");
        }
        removeById(id);
    }

    private SysMenuVO toVO(SysMenu entity) {
        SysMenuVO vo = new SysMenuVO();
        BeanUtils.copyProperties(entity, vo);
        // Copy layout from @TableField(exist = false)
        vo.setLayout(entity.getLayout());
        return vo;
    }

    /**
     * Set layout on each menu VO:
     * - id=1 (首页/dashboard) → "top" (上下布局)
     * - all other menus → "side" (上左右布局)
     */
    private void setLayout(List<SysMenuVO> vos) {
        for (SysMenuVO vo : vos) {
            if (vo.getId() != null && vo.getId() == 1L) {
                vo.setLayout("top");
            } else {
                vo.setLayout("side");
            }
        }
    }
}
