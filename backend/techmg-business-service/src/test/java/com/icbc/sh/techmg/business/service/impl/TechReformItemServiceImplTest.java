package com.icbc.sh.techmg.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.icbc.sh.techmg.business.entity.TechReformItem;
import com.icbc.sh.techmg.business.mapper.TechReformItemMapper;
import com.icbc.sh.techmg.business.vo.TechReformItemVO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * TechReformItemServiceImpl unit test — pageItems / importItems / exportItems
 * Uses Spy to bypass MyBatis-Plus TableInfo dependency, no real DB connection.
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class TechReformItemServiceImplTest {

    @Mock
    private TechReformItemMapper techReformItemMapper;

    @Spy
    @InjectMocks
    private TechReformItemServiceImpl service;

    @Before
    public void setUp() {
        // MyBatis-Plus baseMapper
        ReflectionTestUtils.setField(service, "baseMapper", techReformItemMapper);

        // Spy: stub methods that depend on MyBatis-Plus TableInfo
        doReturn(true).when(service).saveOrUpdate(any());
        doReturn(true).when(service).save(any());
        doReturn(true).when(service).remove(any());
    }

    @Test
    public void shouldPageItemsWithSubtaskId() {
        Page<TechReformItem> page = new Page<>(1, 10);
        TechReformItem item = new TechReformItem();
        item.setId(1L);
        item.setSubtaskId(10L);
        item.setApplicationName("App1");
        page.setRecords(Arrays.asList(item));
        page.setTotal(1);

        doReturn(page).when(service).page(any(Page.class), any(LambdaQueryWrapper.class));

        IPage<TechReformItemVO> result = service.pageItems(new Page<>(1, 10), 10L, null, null, null);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(Long.valueOf(10L), result.getRecords().get(0).getSubtaskId());
    }

    @Test
    public void shouldPageItemsWithAppNameFilter() {
        Page<TechReformItem> page = new Page<>(1, 10);
        TechReformItem item = new TechReformItem();
        item.setId(1L);
        item.setApplicationName("MyApp");
        page.setRecords(Arrays.asList(item));
        page.setTotal(1);

        doReturn(page).when(service).page(any(Page.class), any(LambdaQueryWrapper.class));

        IPage<TechReformItemVO> result = service.pageItems(new Page<>(1, 10), null, "MyApp", null, null);

        assertEquals(1, result.getTotal());
    }

    @Test
    public void shouldPageItemsWithStatusFilter() {
        Page<TechReformItem> page = new Page<>(1, 10);
        TechReformItem item = new TechReformItem();
        item.setId(1L);
        item.setItemStatus("PENDING");
        page.setRecords(Arrays.asList(item));
        page.setTotal(1);

        doReturn(page).when(service).page(any(Page.class), any(LambdaQueryWrapper.class));

        IPage<TechReformItemVO> result = service.pageItems(new Page<>(1, 10), null, null, "PENDING", null);

        assertEquals(1, result.getTotal());
    }

    @Test
    public void shouldPageItemsWithAllFilters() {
        Page<TechReformItem> page = new Page<>(1, 10);
        TechReformItem item = new TechReformItem();
        item.setId(1L);
        item.setSubtaskId(10L);
        item.setApplicationName("AppX");
        item.setItemStatus("IN_PROGRESS");
        page.setRecords(Arrays.asList(item));
        page.setTotal(1);

        doReturn(page).when(service).page(any(Page.class), any(LambdaQueryWrapper.class));

        IPage<TechReformItemVO> result = service.pageItems(
                new Page<>(1, 10), 10L, "AppX", "IN_PROGRESS", "MySQL");

        assertEquals(1, result.getTotal());
    }

    @Test
    public void shouldPageItemsWithNoFilters() {
        Page<TechReformItem> page = new Page<>(1, 10);
        TechReformItem item1 = new TechReformItem();
        item1.setId(1L);
        TechReformItem item2 = new TechReformItem();
        item2.setId(2L);
        page.setRecords(Arrays.asList(item1, item2));
        page.setTotal(2);

        doReturn(page).when(service).page(any(Page.class), any(LambdaQueryWrapper.class));

        IPage<TechReformItemVO> result = service.pageItems(new Page<>(1, 10), null, null, null, null);

        assertEquals(2, result.getTotal());
    }

    @Test
    public void shouldImportItemsWithOverwriteMode() {
        TechReformItem item1 = new TechReformItem();
        item1.setApplicationName("App1");
        item1.setGovernanceItem("MySQL升级");
        TechReformItem item2 = new TechReformItem();
        item2.setApplicationName("App2");
        item2.setGovernanceItem("Redis迁移");

        List<TechReformItem> items = Arrays.asList(item1, item2);

        int count = service.importItems(10L, "OVERWRITE", items);

        assertEquals(2, count);
        assertEquals(Long.valueOf(10L), item1.getSubtaskId());
        assertEquals(Long.valueOf(10L), item2.getSubtaskId());
        assertEquals("PENDING", item1.getItemStatus());
        assertEquals("PENDING", item2.getItemStatus());

        // OVERWRITE mode: deletes old items first
        verify(service).remove(any(LambdaQueryWrapper.class));
        // Then inserts new items
        verify(service, times(2)).save(any(TechReformItem.class));
    }

    @Test
    public void shouldImportItemsWithOverwriteModePreservesExplicitStatus() {
        TechReformItem item = new TechReformItem();
        item.setApplicationName("App1");
        item.setGovernanceItem("MySQL升级");
        item.setItemStatus("IN_PROGRESS");

        List<TechReformItem> items = Arrays.asList(item);

        int count = service.importItems(10L, "OVERWRITE", items);

        assertEquals(1, count);
        // Should preserve explicitly set status
        assertEquals("IN_PROGRESS", item.getItemStatus());
        verify(service).remove(any(LambdaQueryWrapper.class));
        verify(service).save(any(TechReformItem.class));
    }

    @Test
    public void shouldImportItemsWithMergeMode() {
        TechReformItem item1 = new TechReformItem();
        item1.setApplicationName("App1");
        TechReformItem item2 = new TechReformItem();
        item2.setApplicationName("App2");

        List<TechReformItem> items = Arrays.asList(item1, item2);

        int count = service.importItems(10L, "MERGE", items);

        assertEquals(2, count);

        // MERGE mode: should NOT delete old items
        verify(service, never()).remove(any(LambdaQueryWrapper.class));
        verify(service, times(2)).save(any(TechReformItem.class));
    }

    @Test
    public void shouldExportItemsForSubtask() {
        TechReformItem item1 = new TechReformItem();
        item1.setId(1L);
        item1.setSubtaskId(10L);
        TechReformItem item2 = new TechReformItem();
        item2.setId(2L);
        item2.setSubtaskId(10L);

        List<TechReformItem> items = Arrays.asList(item1, item2);
        doReturn(items).when(service).list(any(LambdaQueryWrapper.class));

        List<TechReformItem> result = service.exportItems(10L);

        assertEquals(2, result.size());
        assertEquals(Long.valueOf(10L), result.get(0).getSubtaskId());
        assertEquals(Long.valueOf(10L), result.get(1).getSubtaskId());
        verify(service).list(any(LambdaQueryWrapper.class));
    }

    @Test
    public void shouldExportItemsReturnEmptyWhenNoItems() {
        List<TechReformItem> emptyList = Arrays.asList();
        doReturn(emptyList).when(service).list(any(LambdaQueryWrapper.class));

        List<TechReformItem> result = service.exportItems(10L);

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(service).list(any(LambdaQueryWrapper.class));
    }
}
