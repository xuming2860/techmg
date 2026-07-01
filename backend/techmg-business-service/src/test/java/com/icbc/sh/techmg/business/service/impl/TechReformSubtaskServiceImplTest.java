package com.icbc.sh.techmg.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.icbc.sh.techmg.business.entity.TechReformSubtask;
import com.icbc.sh.techmg.business.mapper.TechReformSubtaskMapper;
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
 * TechReformSubtaskServiceImpl unit test — pageSubtasks / updateStatus
 * Uses Spy to bypass MyBatis-Plus TableInfo dependency, no real DB connection.
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class TechReformSubtaskServiceImplTest {

    @Mock
    private TechReformSubtaskMapper techReformSubtaskMapper;

    @Spy
    @InjectMocks
    private TechReformSubtaskServiceImpl service;

    @Before
    public void setUp() {
        // MyBatis-Plus baseMapper
        ReflectionTestUtils.setField(service, "baseMapper", techReformSubtaskMapper);

        // Spy: stub methods that depend on MyBatis-Plus TableInfo
        doReturn(true).when(service).saveOrUpdate(any());
        doReturn(true).when(service).save(any());
        doReturn(true).when(service).remove(any());
    }

    @Test
    public void shouldPageSubtasksWithParentTaskId() {
        Page<TechReformSubtask> page = new Page<>(1, 10);
        TechReformSubtask subtask = new TechReformSubtask();
        subtask.setId(1L);
        subtask.setParentTaskId(100L);
        subtask.setSubtaskName("子任务1");
        page.setRecords(Arrays.asList(subtask));
        page.setTotal(1);

        doReturn(page).when(service).page(any(Page.class), any(LambdaQueryWrapper.class));

        IPage<TechReformSubtask> result = service.pageSubtasks(new Page<>(1, 10), 100L, null, null);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(Long.valueOf(100L), result.getRecords().get(0).getParentTaskId());
    }

    @Test
    public void shouldPageSubtasksWithStatusFilter() {
        Page<TechReformSubtask> page = new Page<>(1, 10);
        TechReformSubtask subtask = new TechReformSubtask();
        subtask.setId(1L);
        subtask.setStatus("IN_PROGRESS");
        page.setRecords(Arrays.asList(subtask));
        page.setTotal(1);

        doReturn(page).when(service).page(any(Page.class), any(LambdaQueryWrapper.class));

        IPage<TechReformSubtask> result = service.pageSubtasks(new Page<>(1, 10), null, "IN_PROGRESS", null);

        assertEquals(1, result.getTotal());
    }

    @Test
    public void shouldPageSubtasksWithKeywordFilter() {
        Page<TechReformSubtask> page = new Page<>(1, 10);
        TechReformSubtask subtask = new TechReformSubtask();
        subtask.setId(1L);
        subtask.setSubtaskName("Redis集群升级");
        page.setRecords(Arrays.asList(subtask));
        page.setTotal(1);

        doReturn(page).when(service).page(any(Page.class), any(LambdaQueryWrapper.class));

        IPage<TechReformSubtask> result = service.pageSubtasks(new Page<>(1, 10), null, null, "Redis");

        assertEquals(1, result.getTotal());
        assertEquals("Redis集群升级", result.getRecords().get(0).getSubtaskName());
    }

    @Test
    public void shouldPageSubtasksWithNoFilters() {
        Page<TechReformSubtask> page = new Page<>(1, 10);
        TechReformSubtask s1 = new TechReformSubtask();
        s1.setId(1L);
        TechReformSubtask s2 = new TechReformSubtask();
        s2.setId(2L);
        page.setRecords(Arrays.asList(s1, s2));
        page.setTotal(2);

        doReturn(page).when(service).page(any(Page.class), any(LambdaQueryWrapper.class));

        IPage<TechReformSubtask> result = service.pageSubtasks(new Page<>(1, 10), null, null, null);

        assertEquals(2, result.getTotal());
    }

    @Test
    public void shouldUpdateStatusWhenSubtaskExists() {
        TechReformSubtask subtask = new TechReformSubtask();
        subtask.setId(1L);
        subtask.setStatus("PENDING");

        doReturn(subtask).when(service).getById(1L);
        doReturn(true).when(service).updateById(any(TechReformSubtask.class));

        service.updateStatus(1L, "COMPLETED");

        assertEquals("COMPLETED", subtask.getStatus());
        verify(service).getById(1L);
        verify(service).updateById(any(TechReformSubtask.class));
    }

    @Test
    public void shouldNotUpdateStatusWhenSubtaskNotFound() {
        doReturn(null).when(service).getById(999L);

        service.updateStatus(999L, "COMPLETED");

        verify(service).getById(999L);
        verify(service, never()).updateById(any(TechReformSubtask.class));
    }
}
