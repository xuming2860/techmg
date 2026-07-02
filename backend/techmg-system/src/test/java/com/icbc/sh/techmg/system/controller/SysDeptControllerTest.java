package com.icbc.sh.techmg.system.controller;

import com.icbc.sh.techmg.common.model.R;
import com.icbc.sh.techmg.system.dto.SysDeptCreateDTO;
import com.icbc.sh.techmg.system.dto.SysDeptUpdateDTO;
import com.icbc.sh.techmg.system.service.SysDeptService;
import com.icbc.sh.techmg.system.vo.SysDeptVO;
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
 * SysDeptController unit test — tree / getById / create / update / delete
 */
@RunWith(MockitoJUnitRunner.class)
public class SysDeptControllerTest {

    @Mock
    private SysDeptService sysDeptService;

    @InjectMocks
    private SysDeptController sysDeptController;

    private SysDeptVO mockDeptVO;

    @Before
    public void setUp() {
        mockDeptVO = new SysDeptVO();
        mockDeptVO.setId(1L);
        mockDeptVO.setDeptName("上海研发部");
        mockDeptVO.setDeptCode("SH");
        mockDeptVO.setParentId(0L);
        mockDeptVO.setSort(1);
        mockDeptVO.setStatus(1);
    }

    @Test
    public void treeShouldReturnDeptTree() {
        when(sysDeptService.getDeptTree()).thenReturn(Arrays.asList(mockDeptVO));

        R<List<SysDeptVO>> result = sysDeptController.tree();

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
        assertEquals("上海研发部", result.getData().get(0).getDeptName());
    }

    @Test
    public void treeShouldReturnEmptyList() {
        when(sysDeptService.getDeptTree()).thenReturn(Collections.emptyList());

        R<List<SysDeptVO>> result = sysDeptController.tree();

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertTrue(result.getData().isEmpty());
    }

    @Test
    public void getByIdShouldReturnDept() {
        when(sysDeptService.getDeptVO(1L)).thenReturn(mockDeptVO);

        R<SysDeptVO> result = sysDeptController.getById(1L);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("上海研发部", result.getData().getDeptName());
    }

    @Test
    public void createShouldSaveDept() {
        SysDeptCreateDTO dto = new SysDeptCreateDTO();
        dto.setDeptName("测试部");
        dto.setDeptCode("TEST");
        when(sysDeptService.saveDept(any(SysDeptCreateDTO.class))).thenReturn(mockDeptVO);

        R<SysDeptVO> result = sysDeptController.create(dto);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(sysDeptService).saveDept(any(SysDeptCreateDTO.class));
    }

    @Test
    public void updateShouldUpdateDept() {
        SysDeptUpdateDTO dto = new SysDeptUpdateDTO();
        dto.setId(1L);
        dto.setDeptName("上海研发部Updated");
        dto.setDeptCode("SH_UPD");
        when(sysDeptService.updateDept(any(SysDeptUpdateDTO.class))).thenReturn(mockDeptVO);

        R<SysDeptVO> result = sysDeptController.update(dto);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(sysDeptService).updateDept(any(SysDeptUpdateDTO.class));
    }

    @Test
    public void deleteShouldDeleteDept() {
        doNothing().when(sysDeptService).deleteDept(1L);

        R<Void> result = sysDeptController.delete(1L);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(sysDeptService).deleteDept(1L);
    }
}
