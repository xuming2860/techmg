package com.icbc.sh.techmg.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.icbc.sh.techmg.common.constant.ResultCode;
import com.icbc.sh.techmg.common.exception.BusinessException;
import com.icbc.sh.techmg.system.entity.SysOperationLog;
import com.icbc.sh.techmg.system.mapper.SysOperationLogMapper;
import com.icbc.sh.techmg.system.vo.SysOperationLogVO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * SysOperationLogServiceImpl unit test — queryPage / getLogVO
 * Uses Spy to bypass MyBatis-Plus TableInfo dependency, no real DB connection.
 */
@RunWith(MockitoJUnitRunner.class)
public class SysOperationLogServiceImplTest {

    @Mock private SysOperationLogMapper sysOperationLogMapper;

    @Spy
    @InjectMocks
    private SysOperationLogServiceImpl sysOperationLogService;

    private SysOperationLog mockLog;

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(sysOperationLogService, "baseMapper", sysOperationLogMapper);

        mockLog = new SysOperationLog();
        mockLog.setId(1L);
        mockLog.setModule("角色管理");
        mockLog.setOperation("新增角色");
        mockLog.setOperator("admin");
        mockLog.setIp("127.0.0.1");
        mockLog.setCostTime(50L);
        mockLog.setStatus(1);
        mockLog.setCreateTime(LocalDateTime.of(2025, 6, 1, 10, 0, 0));
    }

    // ---- queryPage ----

    @Test
    public void shouldQueryPageWithoutFilters() {
        Page<SysOperationLog> entityPage = new Page<>(1, 10);
        entityPage.setRecords(Arrays.asList(mockLog));
        entityPage.setTotal(1);

        doReturn(entityPage).when(sysOperationLogService).page(any(Page.class), any(LambdaQueryWrapper.class));

        IPage<SysOperationLogVO> result = sysOperationLogService.queryPage(null, null, null, null, 1, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals("角色管理", result.getRecords().get(0).getModule());
        assertEquals("新增角色", result.getRecords().get(0).getOperation());
    }

    @Test
    public void shouldQueryPageWithKeywordFilter() {
        Page<SysOperationLog> entityPage = new Page<>(1, 10);
        entityPage.setRecords(Arrays.asList(mockLog));
        entityPage.setTotal(1);

        doReturn(entityPage).when(sysOperationLogService).page(any(Page.class), any(LambdaQueryWrapper.class));

        IPage<SysOperationLogVO> result = sysOperationLogService.queryPage("角色", null, null, null, 1, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
    }

    @Test
    public void shouldQueryPageWithModuleFilter() {
        Page<SysOperationLog> entityPage = new Page<>(1, 10);
        entityPage.setRecords(Arrays.asList(mockLog));
        entityPage.setTotal(1);

        doReturn(entityPage).when(sysOperationLogService).page(any(Page.class), any(LambdaQueryWrapper.class));

        IPage<SysOperationLogVO> result = sysOperationLogService.queryPage(null, "角色管理", null, null, 1, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
    }

    @Test
    public void shouldQueryPageWithTimeRangeFilter() {
        Page<SysOperationLog> entityPage = new Page<>(1, 10);
        entityPage.setRecords(Arrays.asList(mockLog));
        entityPage.setTotal(1);

        doReturn(entityPage).when(sysOperationLogService).page(any(Page.class), any(LambdaQueryWrapper.class));

        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(2025, 12, 31, 23, 59, 59);

        IPage<SysOperationLogVO> result = sysOperationLogService.queryPage(null, null, start, end, 1, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
    }

    @Test
    public void shouldQueryPageWithAllFilters() {
        Page<SysOperationLog> entityPage = new Page<>(1, 10);
        entityPage.setRecords(Arrays.asList(mockLog));
        entityPage.setTotal(1);

        doReturn(entityPage).when(sysOperationLogService).page(any(Page.class), any(LambdaQueryWrapper.class));

        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(2025, 12, 31, 23, 59, 59);

        IPage<SysOperationLogVO> result = sysOperationLogService.queryPage("角色", "角色管理", start, end, 1, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
    }

    @Test
    public void shouldQueryPageWithEmptyResult() {
        Page<SysOperationLog> entityPage = new Page<>(1, 10);
        entityPage.setRecords(Arrays.asList());
        entityPage.setTotal(0);

        doReturn(entityPage).when(sysOperationLogService).page(any(Page.class), any(LambdaQueryWrapper.class));

        IPage<SysOperationLogVO> result = sysOperationLogService.queryPage(null, null, null, null, 1, 10);

        assertNotNull(result);
        assertEquals(0, result.getTotal());
        assertTrue(result.getRecords().isEmpty());
    }

    @Test
    public void shouldQueryPageWithEmptyKeyword() {
        Page<SysOperationLog> entityPage = new Page<>(1, 10);
        entityPage.setRecords(Arrays.asList(mockLog));
        entityPage.setTotal(1);

        doReturn(entityPage).when(sysOperationLogService).page(any(Page.class), any(LambdaQueryWrapper.class));

        IPage<SysOperationLogVO> result = sysOperationLogService.queryPage("", null, null, null, 1, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
    }

    // ---- getLogVO ----

    @Test
    public void shouldGetLogVOById() {
        doReturn(mockLog).when(sysOperationLogService).getById(1L);

        SysOperationLogVO result = sysOperationLogService.getLogVO(1L);

        assertNotNull(result);
        assertEquals(Long.valueOf(1L), result.getId());
        assertEquals("角色管理", result.getModule());
        assertEquals("新增角色", result.getOperation());
        assertEquals("admin", result.getOperator());
        assertEquals(Long.valueOf(50L), result.getCostTime());
    }

    @Test(expected = BusinessException.class)
    public void shouldThrowWhenGetLogVONotFound() {
        doReturn(null).when(sysOperationLogService).getById(999L);
        sysOperationLogService.getLogVO(999L);
    }
}
