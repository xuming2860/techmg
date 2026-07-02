package com.icbc.sh.techmg.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.icbc.sh.techmg.common.constant.ResultCode;
import com.icbc.sh.techmg.common.exception.BusinessException;
import com.icbc.sh.techmg.system.dto.SysDeptCreateDTO;
import com.icbc.sh.techmg.system.dto.SysDeptUpdateDTO;
import com.icbc.sh.techmg.system.entity.SysDept;
import com.icbc.sh.techmg.system.mapper.SysDeptMapper;
import com.icbc.sh.techmg.system.vo.SysDeptVO;
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
 * SysDeptServiceImpl unit test — getDeptTree / CRUD / deleteDept
 * Uses Spy to bypass MyBatis-Plus TableInfo dependency, no real DB connection.
 */
@RunWith(MockitoJUnitRunner.class)
public class SysDeptServiceImplTest {

    @Mock private SysDeptMapper sysDeptMapper;

    @Spy
    @InjectMocks
    private SysDeptServiceImpl sysDeptService;

    private SysDept deptShanghai;
    private SysDept deptDev;

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(sysDeptService, "baseMapper", sysDeptMapper);

        doReturn(true).when(sysDeptService).save(any(SysDept.class));
        doReturn(true).when(sysDeptService).updateById(any(SysDept.class));
        doReturn(true).when(sysDeptService).removeById(anyLong());

        deptShanghai = buildDept(1L, 0L, "上海研发部", "SH", 1);
        deptDev = buildDept(2L, 1L, "开发部", "DEV", 1);
    }

    private SysDept buildDept(Long id, Long parentId, String name, String code, Integer sort) {
        SysDept d = new SysDept();
        d.setId(id);
        d.setParentId(parentId);
        d.setDeptName(name);
        d.setDeptCode(code);
        d.setSort(sort);
        d.setStatus(1);
        return d;
    }

    // ---- getDeptTree ----

    @Test
    public void shouldGetDeptTree() {
        doReturn(Arrays.asList(deptShanghai, deptDev)).when(sysDeptService).list();

        List<SysDeptVO> result = sysDeptService.getDeptTree();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("上海研发部", result.get(0).getDeptName());
        assertNotNull(result.get(0).getChildren());
        assertEquals(1, result.get(0).getChildren().size());
        assertEquals("开发部", result.get(0).getChildren().get(0).getDeptName());
    }

    @Test
    public void shouldReturnEmptyTreeWhenNoDepts() {
        doReturn(Collections.emptyList()).when(sysDeptService).list();

        List<SysDeptVO> result = sysDeptService.getDeptTree();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ---- getDeptVO ----

    @Test
    public void shouldGetDeptVOById() {
        doReturn(deptShanghai).when(sysDeptService).getById(1L);

        SysDeptVO result = sysDeptService.getDeptVO(1L);

        assertNotNull(result);
        assertEquals(Long.valueOf(1L), result.getId());
        assertEquals("上海研发部", result.getDeptName());
        assertEquals("SH", result.getDeptCode());
    }

    @Test(expected = BusinessException.class)
    public void shouldThrowWhenGetDeptVONotFound() {
        doReturn(null).when(sysDeptService).getById(999L);
        sysDeptService.getDeptVO(999L);
    }

    // ---- saveDept ----

    @Test
    public void shouldSaveDept() {
        SysDeptCreateDTO dto = new SysDeptCreateDTO();
        dto.setDeptName("测试部");
        dto.setDeptCode("TEST");
        dto.setParentId(0L);
        dto.setSort(1);
        dto.setStatus(1);

        SysDeptVO result = sysDeptService.saveDept(dto);

        assertNotNull(result);
        assertEquals("测试部", result.getDeptName());
        assertEquals("TEST", result.getDeptCode());
        verify(sysDeptService).save(any(SysDept.class));
    }

    // ---- updateDept ----

    @Test
    public void shouldUpdateDept() {
        doReturn(deptShanghai).when(sysDeptService).getById(1L);

        SysDeptUpdateDTO dto = new SysDeptUpdateDTO();
        dto.setId(1L);
        dto.setDeptName("上海研发部Updated");
        dto.setDeptCode("SH_UPD");

        SysDeptVO result = sysDeptService.updateDept(dto);

        assertNotNull(result);
        assertEquals("上海研发部Updated", result.getDeptName());
        verify(sysDeptService).updateById(any(SysDept.class));
    }

    @Test(expected = BusinessException.class)
    public void shouldThrowWhenUpdateDeptNotFound() {
        doReturn(null).when(sysDeptService).getById(999L);

        SysDeptUpdateDTO dto = new SysDeptUpdateDTO();
        dto.setId(999L);
        dto.setDeptName("X");
        dto.setDeptCode("X");
        sysDeptService.updateDept(dto);
    }

    // ---- deleteDept ----

    @Test
    public void shouldDeleteDeptWhenNoChildren() {
        doReturn(deptDev).when(sysDeptService).getById(2L);
        doReturn(0L).when(sysDeptService).count(any(LambdaQueryWrapper.class));

        sysDeptService.deleteDept(2L);

        verify(sysDeptService).removeById(2L);
    }

    @Test(expected = BusinessException.class)
    public void shouldThrowWhenDeleteDeptNotFound() {
        doReturn(null).when(sysDeptService).getById(999L);
        sysDeptService.deleteDept(999L);
    }

    @Test(expected = BusinessException.class)
    public void shouldThrowWhenDeleteDeptWithChildren() {
        doReturn(deptShanghai).when(sysDeptService).getById(1L);
        doReturn(1L).when(sysDeptService).count(any(LambdaQueryWrapper.class));

        sysDeptService.deleteDept(1L);
    }
}
