package com.icbc.sh.techmg.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.icbc.sh.techmg.common.constant.ResultCode;
import com.icbc.sh.techmg.common.exception.BusinessException;
import com.icbc.sh.techmg.system.dto.SysRoleCreateDTO;
import com.icbc.sh.techmg.system.dto.SysRoleUpdateDTO;
import com.icbc.sh.techmg.system.entity.SysRole;
import com.icbc.sh.techmg.system.entity.SysRoleMenu;
import com.icbc.sh.techmg.system.entity.SysUserRole;
import com.icbc.sh.techmg.system.mapper.SysRoleMapper;
import com.icbc.sh.techmg.system.mapper.SysRoleMenuMapper;
import com.icbc.sh.techmg.system.mapper.SysUserRoleMapper;
import com.icbc.sh.techmg.system.vo.SysRoleVO;
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
 * SysRoleServiceImpl unit test — CRUD / getRolesByUserId / assignMenus
 * Uses Spy to bypass MyBatis-Plus TableInfo dependency, no real DB connection.
 */
@RunWith(MockitoJUnitRunner.class)
public class SysRoleServiceImplTest {

    @Mock private SysRoleMapper sysRoleMapper;
    @Mock private SysUserRoleMapper sysUserRoleMapper;
    @Mock private SysRoleMenuMapper sysRoleMenuMapper;

    @Spy
    @InjectMocks
    private SysRoleServiceImpl sysRoleService;

    private SysRole mockRole;

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(sysRoleService, "baseMapper", sysRoleMapper);

        doReturn(true).when(sysRoleService).save(any(SysRole.class));
        doReturn(true).when(sysRoleService).updateById(any(SysRole.class));

        mockRole = new SysRole();
        mockRole.setId(1L);
        mockRole.setRoleName("管理员");
        mockRole.setRoleCode("ADMIN");
        mockRole.setDescription("系统管理员");
        mockRole.setSort(1);
        mockRole.setStatus(1);
    }

    // ---- getRoleVO ----

    @Test
    public void shouldGetRoleVOById() {
        doReturn(mockRole).when(sysRoleService).getById(1L);

        SysRoleVO result = sysRoleService.getRoleVO(1L);

        assertNotNull(result);
        assertEquals(Long.valueOf(1L), result.getId());
        assertEquals("管理员", result.getRoleName());
        assertEquals("ADMIN", result.getRoleCode());
    }

    @Test(expected = BusinessException.class)
    public void shouldThrowWhenGetRoleVONotFound() {
        doReturn(null).when(sysRoleService).getById(999L);
        sysRoleService.getRoleVO(999L);
    }

    // ---- saveRole ----

    @Test
    public void shouldSaveRole() {
        SysRoleCreateDTO dto = new SysRoleCreateDTO();
        dto.setRoleName("测试角色");
        dto.setRoleCode("TEST");
        dto.setDescription("测试");
        dto.setSort(2);
        dto.setStatus(1);

        SysRoleVO result = sysRoleService.saveRole(dto);

        assertNotNull(result);
        assertEquals("测试角色", result.getRoleName());
        assertEquals("TEST", result.getRoleCode());
        verify(sysRoleService).save(any(SysRole.class));
    }

    // ---- updateRole ----

    @Test
    public void shouldUpdateRole() {
        doReturn(mockRole).when(sysRoleService).getById(1L);

        SysRoleUpdateDTO dto = new SysRoleUpdateDTO();
        dto.setId(1L);
        dto.setRoleName("管理员Updated");
        dto.setRoleCode("ADMIN_UPD");

        SysRoleVO result = sysRoleService.updateRole(dto);

        assertNotNull(result);
        assertEquals("管理员Updated", result.getRoleName());
        verify(sysRoleService).updateById(any(SysRole.class));
    }

    @Test(expected = BusinessException.class)
    public void shouldThrowWhenUpdateRoleNotFound() {
        doReturn(null).when(sysRoleService).getById(999L);

        SysRoleUpdateDTO dto = new SysRoleUpdateDTO();
        dto.setId(999L);
        dto.setRoleName("X");
        sysRoleService.updateRole(dto);
    }

    // ---- getRolesByUserId ----

    @Test
    public void shouldGetRolesByUserId() {
        SysUserRole ur = new SysUserRole();
        ur.setUserId(1L);
        ur.setRoleId(1L);
        when(sysUserRoleMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Arrays.asList(ur));

        doReturn(Arrays.asList(mockRole)).when(sysRoleService).listByIds(anyList());

        List<SysRoleVO> result = sysRoleService.getRolesByUserId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ADMIN", result.get(0).getRoleCode());
        verify(sysUserRoleMapper).selectList(any(LambdaQueryWrapper.class));
    }

    @Test
    public void shouldReturnEmptyWhenUserHasNoRoles() {
        when(sysUserRoleMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        List<SysRoleVO> result = sysRoleService.getRolesByUserId(999L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ---- getMenuIdsByRoleId ----

    @Test
    public void shouldGetMenuIdsByRoleId() {
        SysRoleMenu rm1 = new SysRoleMenu();
        rm1.setRoleId(1L);
        rm1.setMenuId(101L);
        SysRoleMenu rm2 = new SysRoleMenu();
        rm2.setRoleId(1L);
        rm2.setMenuId(102L);

        when(sysRoleMenuMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Arrays.asList(rm1, rm2));

        List<Long> result = sysRoleService.getMenuIdsByRoleId(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(101L));
        assertTrue(result.contains(102L));
    }

    @Test
    public void shouldReturnEmptyMenuIdsWhenNoMenusAssigned() {
        when(sysRoleMenuMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        List<Long> result = sysRoleService.getMenuIdsByRoleId(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ---- assignMenus ----

    @Test
    public void shouldAssignMenusClearAndInsert() {
        doReturn(1).when(sysRoleMenuMapper).delete(any(LambdaQueryWrapper.class));
        doReturn(1).when(sysRoleMenuMapper).insert(any(SysRoleMenu.class));

        sysRoleService.assignMenus(1L, Arrays.asList(101L, 102L));

        verify(sysRoleMenuMapper).delete(any(LambdaQueryWrapper.class));
        verify(sysRoleMenuMapper, times(2)).insert(any(SysRoleMenu.class));
    }

    @Test
    public void shouldAssignMenusWithEmptyListOnlyClear() {
        doReturn(0).when(sysRoleMenuMapper).delete(any(LambdaQueryWrapper.class));

        sysRoleService.assignMenus(1L, Collections.emptyList());

        verify(sysRoleMenuMapper).delete(any(LambdaQueryWrapper.class));
        verify(sysRoleMenuMapper, never()).insert(any(SysRoleMenu.class));
    }

    @Test
    public void shouldAssignMenusWithNullListOnlyClear() {
        doReturn(0).when(sysRoleMenuMapper).delete(any(LambdaQueryWrapper.class));

        sysRoleService.assignMenus(1L, null);

        verify(sysRoleMenuMapper).delete(any(LambdaQueryWrapper.class));
        verify(sysRoleMenuMapper, never()).insert(any(SysRoleMenu.class));
    }
}
