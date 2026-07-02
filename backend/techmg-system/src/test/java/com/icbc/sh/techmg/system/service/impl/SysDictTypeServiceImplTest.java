package com.icbc.sh.techmg.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.icbc.sh.techmg.common.constant.ResultCode;
import com.icbc.sh.techmg.common.exception.BusinessException;
import com.icbc.sh.techmg.system.dto.SysDictTypeCreateDTO;
import com.icbc.sh.techmg.system.dto.SysDictTypeUpdateDTO;
import com.icbc.sh.techmg.system.entity.SysDictType;
import com.icbc.sh.techmg.system.mapper.SysDictTypeMapper;
import com.icbc.sh.techmg.system.vo.SysDictTypeVO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * SysDictTypeServiceImpl unit test — getDictTypeVO / saveDictType / updateDictType / deleteDictType
 * Uses Spy to bypass MyBatis-Plus TableInfo dependency, no real DB connection.
 */
@RunWith(MockitoJUnitRunner.class)
public class SysDictTypeServiceImplTest {

    @Mock private SysDictTypeMapper sysDictTypeMapper;

    @Spy
    @InjectMocks
    private SysDictTypeServiceImpl sysDictTypeService;

    private SysDictType mockDictType;

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(sysDictTypeService, "baseMapper", sysDictTypeMapper);

        doReturn(true).when(sysDictTypeService).save(any(SysDictType.class));
        doReturn(true).when(sysDictTypeService).updateById(any(SysDictType.class));
        doReturn(true).when(sysDictTypeService).removeById(anyLong());

        mockDictType = new SysDictType();
        mockDictType.setId(1L);
        mockDictType.setDictName("性别");
        mockDictType.setDictType("gender");
        mockDictType.setStatus(1);
        mockDictType.setRemark("性别字典");
    }

    // ---- getDictTypeVO ----

    @Test
    public void shouldGetDictTypeVOById() {
        doReturn(mockDictType).when(sysDictTypeService).getById(1L);

        SysDictTypeVO result = sysDictTypeService.getDictTypeVO(1L);

        assertNotNull(result);
        assertEquals(Long.valueOf(1L), result.getId());
        assertEquals("性别", result.getDictName());
        assertEquals("gender", result.getDictType());
    }

    @Test(expected = BusinessException.class)
    public void shouldThrowWhenGetDictTypeVONotFound() {
        doReturn(null).when(sysDictTypeService).getById(999L);
        sysDictTypeService.getDictTypeVO(999L);
    }

    // ---- saveDictType ----

    @Test
    public void shouldSaveDictType() {
        doReturn(0L).when(sysDictTypeService).count(any(LambdaQueryWrapper.class));

        SysDictTypeCreateDTO dto = new SysDictTypeCreateDTO();
        dto.setDictName("状态");
        dto.setDictType("status");
        dto.setStatus(1);
        dto.setRemark("状态字典");

        SysDictTypeVO result = sysDictTypeService.saveDictType(dto);

        assertNotNull(result);
        assertEquals("状态", result.getDictName());
        assertEquals("status", result.getDictType());
        verify(sysDictTypeService).save(any(SysDictType.class));
    }

    @Test(expected = BusinessException.class)
    public void shouldThrowWhenSaveDictTypeDuplicate() {
        doReturn(1L).when(sysDictTypeService).count(any(LambdaQueryWrapper.class));

        SysDictTypeCreateDTO dto = new SysDictTypeCreateDTO();
        dto.setDictName("性别");
        dto.setDictType("gender");
        sysDictTypeService.saveDictType(dto);
    }

    // ---- updateDictType ----

    @Test
    public void shouldUpdateDictType() {
        doReturn(mockDictType).when(sysDictTypeService).getById(1L);

        SysDictTypeUpdateDTO dto = new SysDictTypeUpdateDTO();
        dto.setId(1L);
        dto.setDictName("性别Updated");
        dto.setDictType("gender_updated");

        SysDictTypeVO result = sysDictTypeService.updateDictType(dto);

        assertNotNull(result);
        assertEquals("性别Updated", result.getDictName());
        verify(sysDictTypeService).updateById(any(SysDictType.class));
    }

    @Test(expected = BusinessException.class)
    public void shouldThrowWhenUpdateDictTypeNotFound() {
        doReturn(null).when(sysDictTypeService).getById(999L);

        SysDictTypeUpdateDTO dto = new SysDictTypeUpdateDTO();
        dto.setId(999L);
        dto.setDictName("X");
        dto.setDictType("x");
        sysDictTypeService.updateDictType(dto);
    }

    // ---- deleteDictType ----

    @Test
    public void shouldDeleteDictType() {
        doReturn(mockDictType).when(sysDictTypeService).getById(1L);

        sysDictTypeService.deleteDictType(1L);

        verify(sysDictTypeService).removeById(1L);
    }

    @Test(expected = BusinessException.class)
    public void shouldThrowWhenDeleteDictTypeNotFound() {
        doReturn(null).when(sysDictTypeService).getById(999L);
        sysDictTypeService.deleteDictType(999L);
    }
}
