package com.icbc.sh.techmg.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.icbc.sh.techmg.common.constant.ResultCode;
import com.icbc.sh.techmg.common.exception.BusinessException;
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
import com.icbc.sh.techmg.system.vo.SysMenuVO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * SysMenuServiceImpl unit test — getMenuTree / getMenuTreeByUserId / CRUD / deleteMenu
 * Uses Spy to bypass MyBatis-Plus TableInfo dependency, no real DB connection.
 */
@RunWith(MockitoJUnitRunner.class)
public class SysMenuServiceImplTest {

    @Mock private SysMenuMapper sysMenuMapper;
    @Mock private SysRoleMenuMapper sysRoleMenuMapper;
    @Mock private SysUserRoleMapper sysUserRoleMapper;
    @Mock private SysRoleMapper sysRoleMapper;

    @Spy
    @InjectMocks
    private SysMenuServiceImpl sysMenuService;

    private SysMenu menuDashboard;
    private SysMenu menuTech;
    private SysMenu menuReform;

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(sysMenuService, "baseMapper", sysMenuMapper);

        doReturn(true).when(sysMenuService).save(any(SysMenu.class));
        doReturn(true).when(sysMenuService).updateById(any(SysMenu.class));
        doReturn(true).when(sysMenuService).removeById(anyLong());

        menuDashboard = buildMenu(1L, 0L, "首页", "dashboard", 1, 0);
        menuTech = buildMenu(2L, 0L, "技术管理", "/tech", 2, 0);
        menuReform = buildMenu(3L, 2L, "技改任务", "/tech/reform", 1, 1);
    }

    private SysMenu buildMenu(Long id, Long parentId, String name, String path, Integer sort, Integer type) {
        SysMenu m = new SysMenu();
        m.setId(id);
        m.setParentId(parentId);
        m.setMenuName(name);
        m.setPath(path);
        m.setSort(sort);
        m.setType(type);
        m.setStatus(1);
        m.setVisible(1);
        return m;
    }

    // ---- getMenuTree ----

    @Test
    public void shouldGetMenuTree() {
        doReturn(Arrays.asList(menuDashboard, menuTech, menuReform)).when(sysMenuService).list();

        List<SysMenuVO> result = sysMenuService.getMenuTree();

        assertNotNull(result);
        // Root nodes at parentId=0
        assertEquals(2, result.size());
        // Dashboard should have layout "top"
        SysMenuVO dashboard = result.stream().filter(v -> v.getId() == 1L).findFirst().orElse(null);
        assertNotNull(dashboard);
        assertEquals("top", dashboard.getLayout());
        // Tech should have children
        SysMenuVO tech = result.stream().filter(v -> v.getId() == 2L).findFirst().orElse(null);
        assertNotNull(tech);
        assertEquals("side", tech.getLayout());
        assertNotNull(tech.getChildren());
        assertEquals(1, tech.getChildren().size());
        assertEquals("技改任务", tech.getChildren().get(0).getMenuName());
    }

    @Test
    public void shouldReturnEmptyTreeWhenNoMenus() {
        doReturn(Collections.emptyList()).when(sysMenuService).list();

        List<SysMenuVO> result = sysMenuService.getMenuTree();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ---- getMenusByRoleId ----

    @Test
    public void shouldGetMenusByRoleId() {
        SysRoleMenu rm = new SysRoleMenu();
        rm.setRoleId(1L);
        rm.setMenuId(1L);
        when(sysRoleMenuMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Arrays.asList(rm));

        doReturn(Arrays.asList(menuDashboard)).when(sysMenuService).listByIds(anyList());

        List<SysMenuVO> result = sysMenuService.getMenusByRoleId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("首页", result.get(0).getMenuName());
    }

    @Test
    public void shouldReturnEmptyMenusByRoleIdWhenNoMenus() {
        when(sysRoleMenuMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        List<SysMenuVO> result = sysMenuService.getMenusByRoleId(999L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ---- getMenuTreeByUserId ----

    @Test
    public void shouldReturnAllMenusForPlatformAdmin() {
        // user has platform admin role
        SysUserRole ur = new SysUserRole();
        ur.setUserId(1L);
        ur.setRoleId(1L);
        when(sysUserRoleMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Arrays.asList(ur));

        SysRole adminRole = new SysRole();
        adminRole.setId(1L);
        adminRole.setRoleCode("PLATFORM_ADMIN");
        when(sysRoleMapper.selectBatchIds(anyList())).thenReturn(Arrays.asList(adminRole));

        doReturn(Arrays.asList(menuDashboard, menuTech, menuReform)).when(sysMenuService).list();

        List<SysMenuVO> result = sysMenuService.getMenuTreeByUserId(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void shouldReturnFilteredMenusForNonAdmin() {
        SysUserRole ur = new SysUserRole();
        ur.setUserId(2L);
        ur.setRoleId(2L);
        when(sysUserRoleMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Arrays.asList(ur));

        SysRole normalRole = new SysRole();
        normalRole.setId(2L);
        normalRole.setRoleCode("NORMAL");
        when(sysRoleMapper.selectBatchIds(anyList())).thenReturn(Arrays.asList(normalRole));

        SysRoleMenu rm = new SysRoleMenu();
        rm.setRoleId(2L);
        rm.setMenuId(1L);
        when(sysRoleMenuMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Arrays.asList(rm));

        doReturn(Arrays.asList(menuDashboard, menuTech, menuReform)).when(sysMenuService).list();

        List<SysMenuVO> result = sysMenuService.getMenuTreeByUserId(2L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("首页", result.get(0).getMenuName());
    }

    @Test
    public void shouldReturnEmptyWhenUserHasNoRoles() {
        when(sysUserRoleMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        List<SysMenuVO> result = sysMenuService.getMenuTreeByUserId(999L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldReturnEmptyWhenRoleHasNoMenus() {
        SysUserRole ur = new SysUserRole();
        ur.setUserId(2L);
        ur.setRoleId(2L);
        when(sysUserRoleMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Arrays.asList(ur));

        SysRole normalRole = new SysRole();
        normalRole.setId(2L);
        normalRole.setRoleCode("NORMAL");
        when(sysRoleMapper.selectBatchIds(anyList())).thenReturn(Arrays.asList(normalRole));

        when(sysRoleMenuMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        List<SysMenuVO> result = sysMenuService.getMenuTreeByUserId(2L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldIncludeParentMenuForChildAccess() {
        // User has access only to child menu (技改任务 id=3, parent=2)
        SysUserRole ur = new SysUserRole();
        ur.setUserId(2L);
        ur.setRoleId(2L);
        when(sysUserRoleMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Arrays.asList(ur));

        SysRole normalRole = new SysRole();
        normalRole.setId(2L);
        normalRole.setRoleCode("NORMAL");
        when(sysRoleMapper.selectBatchIds(anyList())).thenReturn(Arrays.asList(normalRole));

        SysRoleMenu rm = new SysRoleMenu();
        rm.setRoleId(2L);
        rm.setMenuId(3L);
        when(sysRoleMenuMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Arrays.asList(rm));

        doReturn(Arrays.asList(menuDashboard, menuTech, menuReform)).when(sysMenuService).list();

        List<SysMenuVO> result = sysMenuService.getMenuTreeByUserId(2L);

        assertNotNull(result);
        // Should have 技术管理 as parent, containing 技改任务 as child
        assertEquals(1, result.size());
        assertEquals("技术管理", result.get(0).getMenuName());
        assertNotNull(result.get(0).getChildren());
        assertEquals(1, result.get(0).getChildren().size());
        assertEquals("技改任务", result.get(0).getChildren().get(0).getMenuName());
    }

    // ---- getMenuVO ----

    @Test
    public void shouldGetMenuVOById() {
        doReturn(menuDashboard).when(sysMenuService).getById(1L);

        SysMenuVO result = sysMenuService.getMenuVO(1L);

        assertNotNull(result);
        assertEquals(Long.valueOf(1L), result.getId());
        assertEquals("首页", result.getMenuName());
    }

    @Test(expected = BusinessException.class)
    public void shouldThrowWhenGetMenuVONotFound() {
        doReturn(null).when(sysMenuService).getById(999L);
        sysMenuService.getMenuVO(999L);
    }

    // ---- saveMenu ----

    @Test
    public void shouldSaveMenu() {
        SysMenuCreateDTO dto = new SysMenuCreateDTO();
        dto.setMenuName("新菜单");
        dto.setPath("/new");
        dto.setParentId(0L);
        dto.setSort(10);
        dto.setType(0);
        dto.setStatus(1);
        dto.setVisible(1);

        SysMenuVO result = sysMenuService.saveMenu(dto);

        assertNotNull(result);
        assertEquals("新菜单", result.getMenuName());
        verify(sysMenuService).save(any(SysMenu.class));
    }

    // ---- updateMenu ----

    @Test
    public void shouldUpdateMenu() {
        doReturn(menuDashboard).when(sysMenuService).getById(1L);

        SysMenuUpdateDTO dto = new SysMenuUpdateDTO();
        dto.setId(1L);
        dto.setMenuName("首页Updated");
        dto.setPath("/dashboard-new");

        SysMenuVO result = sysMenuService.updateMenu(dto);

        assertNotNull(result);
        assertEquals("首页Updated", result.getMenuName());
        verify(sysMenuService).updateById(any(SysMenu.class));
    }

    @Test(expected = BusinessException.class)
    public void shouldThrowWhenUpdateMenuNotFound() {
        doReturn(null).when(sysMenuService).getById(999L);

        SysMenuUpdateDTO dto = new SysMenuUpdateDTO();
        dto.setId(999L);
        dto.setMenuName("X");
        sysMenuService.updateMenu(dto);
    }

    // ---- deleteMenu ----

    @Test
    public void shouldDeleteMenuWhenNoChildren() {
        doReturn(menuDashboard).when(sysMenuService).getById(1L);
        doReturn(0L).when(sysMenuService).count(any(LambdaQueryWrapper.class));

        sysMenuService.deleteMenu(1L);

        verify(sysMenuService).removeById(1L);
    }

    @Test(expected = BusinessException.class)
    public void shouldThrowWhenDeleteMenuNotFound() {
        doReturn(null).when(sysMenuService).getById(999L);
        sysMenuService.deleteMenu(999L);
    }

    @Test(expected = BusinessException.class)
    public void shouldThrowWhenDeleteMenuWithChildren() {
        doReturn(menuTech).when(sysMenuService).getById(2L);
        doReturn(1L).when(sysMenuService).count(any(LambdaQueryWrapper.class));

        sysMenuService.deleteMenu(2L);
    }
}
