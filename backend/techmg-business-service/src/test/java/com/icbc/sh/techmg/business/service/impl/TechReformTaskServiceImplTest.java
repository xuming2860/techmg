package com.icbc.sh.techmg.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.icbc.sh.techmg.business.entity.TechReformTask;
import com.icbc.sh.techmg.business.mapper.TechReformTaskMapper;
import com.icbc.sh.techmg.business.vo.TechReformTaskVO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * TechReformTaskServiceImpl unit test — pageTasks / updateStatus
 * Uses Spy to bypass MyBatis-Plus TableInfo dependency, no real DB connection.
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class TechReformTaskServiceImplTest {

    @Mock
    private TechReformTaskMapper techReformTaskMapper;

    @Spy
    @InjectMocks
    private TechReformTaskServiceImpl service;

    @Before
    public void setUp() {
        // MyBatis-Plus baseMapper
        ReflectionTestUtils.setField(service, "baseMapper", techReformTaskMapper);

        // Spy: stub methods that depend on MyBatis-Plus TableInfo
        doReturn(true).when(service).saveOrUpdate(any());
        doReturn(true).when(service).save(any());
        doReturn(true).when(service).remove(any());
    }

    @Test
    public void shouldPageTasksWithKeywordFilter() {
        Page<TechReformTask> page = new Page<>(1, 10);
        TechReformTask task = new TechReformTask();
        task.setId(1L);
        task.setTaskName("MySQL升级");
        page.setRecords(Arrays.asList(task));
        page.setTotal(1);

        doReturn(page).when(service).page(any(Page.class), any(LambdaQueryWrapper.class));

        IPage<TechReformTaskVO> result = service.pageTasks(new Page<>(1, 10), "MySQL", null, null);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals("MySQL升级", result.getRecords().get(0).getTaskName());
    }

    @Test
    public void shouldPageTasksWithCategoryFilter() {
        Page<TechReformTask> page = new Page<>(1, 10);
        TechReformTask task = new TechReformTask();
        task.setId(1L);
        task.setTaskCategory("DB");
        page.setRecords(Arrays.asList(task));
        page.setTotal(1);

        doReturn(page).when(service).page(any(Page.class), any(LambdaQueryWrapper.class));

        IPage<TechReformTaskVO> result = service.pageTasks(new Page<>(1, 10), null, "DB", null);

        assertEquals(1, result.getTotal());
    }

    @Test
    public void shouldPageTasksWithStatusFilter() {
        Page<TechReformTask> page = new Page<>(1, 10);
        TechReformTask task = new TechReformTask();
        task.setId(1L);
        task.setStatus("IN_PROGRESS");
        page.setRecords(Arrays.asList(task));
        page.setTotal(1);

        doReturn(page).when(service).page(any(Page.class), any(LambdaQueryWrapper.class));

        IPage<TechReformTaskVO> result = service.pageTasks(new Page<>(1, 10), null, null, "IN_PROGRESS");

        assertEquals(1, result.getTotal());
    }

    @Test
    public void shouldPageTasksWithAllFilters() {
        Page<TechReformTask> page = new Page<>(1, 10);
        TechReformTask task = new TechReformTask();
        task.setId(1L);
        task.setTaskName("Redis迁移");
        task.setTaskCategory("CACHE");
        task.setStatus("PENDING");
        page.setRecords(Arrays.asList(task));
        page.setTotal(1);

        doReturn(page).when(service).page(any(Page.class), any(LambdaQueryWrapper.class));

        IPage<TechReformTaskVO> result = service.pageTasks(
                new Page<>(1, 10), "Redis", "CACHE", "PENDING");

        assertEquals(1, result.getTotal());
        TechReformTaskVO record = result.getRecords().get(0);
        assertEquals("Redis迁移", record.getTaskName());
        assertEquals("CACHE", record.getTaskCategory());
        assertEquals("PENDING", record.getStatus());
    }

    @Test
    public void shouldPageTasksWithNoFilters() {
        Page<TechReformTask> page = new Page<>(1, 10);
        TechReformTask task1 = new TechReformTask();
        task1.setId(1L);
        TechReformTask task2 = new TechReformTask();
        task2.setId(2L);
        page.setRecords(Arrays.asList(task1, task2));
        page.setTotal(2);

        doReturn(page).when(service).page(any(Page.class), any(LambdaQueryWrapper.class));

        IPage<TechReformTaskVO> result = service.pageTasks(new Page<>(1, 10), null, null, null);

        assertEquals(2, result.getTotal());
    }

    @Test
    public void shouldUpdateStatusWhenTaskExists() {
        TechReformTask task = new TechReformTask();
        task.setId(1L);
        task.setStatus("PENDING");

        doReturn(task).when(service).getById(1L);
        doReturn(true).when(service).updateById(any(TechReformTask.class));

        service.updateStatus(1L, "IN_PROGRESS");

        assertEquals("IN_PROGRESS", task.getStatus());
        verify(service).getById(1L);
        verify(service).updateById(any(TechReformTask.class));
    }

    @Test(expected = com.icbc.sh.techmg.common.exception.BusinessException.class)
    public void shouldThrowWhenUpdateStatusTaskNotFound() {
        doReturn(null).when(service).getById(999L);

        service.updateStatus(999L, "IN_PROGRESS");

        verify(service).getById(999L);
        verify(service, never()).updateById(any(TechReformTask.class));
    }
}
