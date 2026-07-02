package com.icbc.sh.techmg.system.controller;

import com.icbc.sh.techmg.common.model.R;
import com.icbc.sh.techmg.system.dto.SysMenuCreateDTO;
import com.icbc.sh.techmg.system.dto.SysMenuUpdateDTO;
import com.icbc.sh.techmg.system.service.SysMenuService;
import com.icbc.sh.techmg.system.service.SysUserService;
import com.icbc.sh.techmg.system.vo.SysMenuVO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * SysMenuController unit test — tree / getById / create / update / delete
 */
@RunWith(MockitoJUnitRunner.class)
public class SysMenuControllerTest {

    @Mock
    private SysMenuService sysMenuService;

    @Mock
    private SysUserService sysUserService;

    @InjectMocks
    private SysMenuController sysMenuController;

    private SysMenuVO mockMenuVO;

    @Before
    public void setUp() {
        mockMenuVO = new SysMenuVO();
        mockMenuVO.setId(1L);
        mockMenuVO.setParentId(0L);
        mockMenuVO.setMenuName("首页");
        mockMenuVO.setPath("/dashboard");
        mockMenuVO.setType(0);
        mockMenuVO.setSort(1);
        mockMenuVO.setStatus(1);
        mockMenuVO.setVisible(1);
    }

    @Test
    public void treeShouldReturnMenuTree() {
        when(sysMenuService.getMenuTree()).thenReturn(Arrays.asList(mockMenuVO));

        R<List<SysMenuVO>> result = sysMenuController.tree();

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
        assertEquals("首页", result.getData().get(0).getMenuName());
    }

    @Test
    public void treeShouldReturnEmptyList() {
        when(sysMenuService.getMenuTree()).thenReturn(Collections.emptyList());

        R<List<SysMenuVO>> result = sysMenuController.tree();

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertTrue(result.getData().isEmpty());
    }

    @Test
    public void getByIdShouldReturnMenu() {
        when(sysMenuService.getMenuVO(1L)).thenReturn(mockMenuVO);

        R<SysMenuVO> result = sysMenuController.getById(1L);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("首页", result.getData().getMenuName());
    }

    @Test
    public void createShouldSaveMenu() {
        SysMenuCreateDTO dto = new SysMenuCreateDTO();
        dto.setMenuName("新菜单");
        dto.setPath("/new");
        dto.setParentId(0L);
        when(sysMenuService.saveMenu(any(SysMenuCreateDTO.class))).thenReturn(mockMenuVO);

        R<SysMenuVO> result = sysMenuController.create(dto);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(sysMenuService).saveMenu(any(SysMenuCreateDTO.class));
    }

    @Test
    public void updateShouldUpdateMenu() {
        SysMenuUpdateDTO dto = new SysMenuUpdateDTO();
        dto.setId(1L);
        dto.setMenuName("首页Updated");
        when(sysMenuService.updateMenu(any(SysMenuUpdateDTO.class))).thenReturn(mockMenuVO);

        R<SysMenuVO> result = sysMenuController.update(dto);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(sysMenuService).updateMenu(any(SysMenuUpdateDTO.class));
    }

    @Test
    public void deleteShouldDeleteMenu() {
        doNothing().when(sysMenuService).deleteMenu(1L);

        R<Void> result = sysMenuController.delete(1L);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(sysMenuService).deleteMenu(1L);
    }
}
