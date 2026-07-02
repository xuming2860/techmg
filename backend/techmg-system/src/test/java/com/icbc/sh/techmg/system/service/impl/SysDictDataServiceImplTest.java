package com.icbc.sh.techmg.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.icbc.sh.techmg.common.constant.ResultCode;
import com.icbc.sh.techmg.common.exception.BusinessException;
import com.icbc.sh.techmg.system.dto.SysDictDataCreateDTO;
import com.icbc.sh.techmg.system.dto.SysDictDataUpdateDTO;
import com.icbc.sh.techmg.system.entity.SysDictData;
import com.icbc.sh.techmg.system.mapper.SysDictDataMapper;
import com.icbc.sh.techmg.system.vo.SysDictDataVO;
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
 * SysDictDataServiceImpl unit test — getByDictType / getDictDataVO / saveDictData / updateDictData / deleteDictData
 * Uses Spy to bypass MyBatis-Plus TableInfo dependency, no real DB connection.
 */
@RunWith(MockitoJUnitRunner.class)
public class SysDictDataServiceImplTest {

    @Mock private SysDictDataMapper sysDictDataMapper;

    @Spy
    @InjectMocks
    private SysDictDataServiceImpl sysDictDataService;

    private SysDictData maleData;
    private SysDictData femaleData;

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(sysDictDataService, "baseMapper", sysDictDataMapper);

        doReturn(true).when(sysDictDataService).save(any(SysDictData.class));
        doReturn(true).when(sysDictDataService).updateById(any(SysDictData.class));
        doReturn(true).when(sysDictDataService).removeById(anyLong());

        maleData = new SysDictData();
        maleData.setId(1L);
        maleData.setDictType("gender");
        maleData.setDictLabel("男");
        maleData.setDictValue("M");
        maleData.setSort(1);
        maleData.setStatus(1);

        femaleData = new SysDictData();
        femaleData.setId(2L);
        femaleData.setDictType("gender");
        femaleData.setDictLabel("女");
        femaleData.setDictValue("F");
        femaleData.setSort(2);
        femaleData.setStatus(1);
    }

    // ---- getByDictType ----

    @Test
    public void shouldGetByDictType() {
        doReturn(Arrays.asList(maleData, femaleData)).when(sysDictDataService).list(any(LambdaQueryWrapper.class));

        List<SysDictDataVO> result = sysDictDataService.getByDictType("gender");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("男", result.get(0).getDictLabel());
        assertEquals("女", result.get(1).getDictLabel());
    }

    @Test
    public void shouldReturnEmptyListForUnknownDictType() {
        doReturn(Collections.emptyList()).when(sysDictDataService).list(any(LambdaQueryWrapper.class));

        List<SysDictDataVO> result = sysDictDataService.getByDictType("unknown");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ---- getDictDataVO ----

    @Test
    public void shouldGetDictDataVOById() {
        doReturn(maleData).when(sysDictDataService).getById(1L);

        SysDictDataVO result = sysDictDataService.getDictDataVO(1L);

        assertNotNull(result);
        assertEquals(Long.valueOf(1L), result.getId());
        assertEquals("男", result.getDictLabel());
        assertEquals("M", result.getDictValue());
    }

    @Test(expected = BusinessException.class)
    public void shouldThrowWhenGetDictDataVONotFound() {
        doReturn(null).when(sysDictDataService).getById(999L);
        sysDictDataService.getDictDataVO(999L);
    }

    // ---- saveDictData ----

    @Test
    public void shouldSaveDictData() {
        SysDictDataCreateDTO dto = new SysDictDataCreateDTO();
        dto.setDictType("gender");
        dto.setDictLabel("未知");
        dto.setDictValue("U");
        dto.setSort(3);
        dto.setStatus(1);

        SysDictDataVO result = sysDictDataService.saveDictData(dto);

        assertNotNull(result);
        assertEquals("未知", result.getDictLabel());
        assertEquals("U", result.getDictValue());
        verify(sysDictDataService).save(any(SysDictData.class));
    }

    // ---- updateDictData ----

    @Test
    public void shouldUpdateDictData() {
        doReturn(maleData).when(sysDictDataService).getById(1L);

        SysDictDataUpdateDTO dto = new SysDictDataUpdateDTO();
        dto.setId(1L);
        dto.setDictLabel("男性");
        dto.setDictValue("M");

        SysDictDataVO result = sysDictDataService.updateDictData(dto);

        assertNotNull(result);
        assertEquals("男性", result.getDictLabel());
        verify(sysDictDataService).updateById(any(SysDictData.class));
    }

    @Test(expected = BusinessException.class)
    public void shouldThrowWhenUpdateDictDataNotFound() {
        doReturn(null).when(sysDictDataService).getById(999L);

        SysDictDataUpdateDTO dto = new SysDictDataUpdateDTO();
        dto.setId(999L);
        dto.setDictLabel("X");
        dto.setDictValue("X");
        sysDictDataService.updateDictData(dto);
    }

    // ---- deleteDictData ----

    @Test
    public void shouldDeleteDictData() {
        doReturn(maleData).when(sysDictDataService).getById(1L);

        sysDictDataService.deleteDictData(1L);

        verify(sysDictDataService).removeById(1L);
    }

    @Test(expected = BusinessException.class)
    public void shouldThrowWhenDeleteDictDataNotFound() {
        doReturn(null).when(sysDictDataService).getById(999L);
        sysDictDataService.deleteDictData(999L);
    }
}
