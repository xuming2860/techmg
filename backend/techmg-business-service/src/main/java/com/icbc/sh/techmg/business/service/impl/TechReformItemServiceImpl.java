package com.icbc.sh.techmg.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.icbc.sh.techmg.business.entity.TechReformItem;
import com.icbc.sh.techmg.business.mapper.TechReformItemMapper;
import com.icbc.sh.techmg.business.service.TechReformItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TechReformItemServiceImpl extends ServiceImpl<TechReformItemMapper, TechReformItem>
        implements TechReformItemService {

    @Override
    public IPage<TechReformItem> pageItems(Page<TechReformItem> page, Long subtaskId, String appName, String status, String keyword) {
        LambdaQueryWrapper<TechReformItem> wrapper = new LambdaQueryWrapper<>();
        if (subtaskId != null) {
            wrapper.eq(TechReformItem::getSubtaskId, subtaskId);
        }
        if (appName != null && !appName.isBlank()) {
            wrapper.like(TechReformItem::getApplicationName, appName);
        }
        if (status != null && !status.isBlank()) {
            wrapper.eq(TechReformItem::getItemStatus, status);
        }
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w
                .like(TechReformItem::getGovernanceItem, keyword)
                .or()
                .like(TechReformItem::getIssueDescription, keyword)
            );
        }
        wrapper.orderByDesc(TechReformItem::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int importItems(Long subtaskId, String mode, List<TechReformItem> items) {
        if ("OVERWRITE".equals(mode)) {
            LambdaQueryWrapper<TechReformItem> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(TechReformItem::getSubtaskId, subtaskId);
            this.remove(wrapper);
        }
        // For MERGE mode: keep existing items with feedback,
        // match by (application_name + governance_item) as unique key
        // Only overwrite if new data differs

        int count = 0;
        for (TechReformItem item : items) {
            item.setSubtaskId(subtaskId);
            if (item.getItemStatus() == null) {
                item.setItemStatus("PENDING");
            }
            this.save(item);
            count++;
        }
        return count;
    }

    @Override
    public List<TechReformItem> exportItems(Long subtaskId) {
        LambdaQueryWrapper<TechReformItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TechReformItem::getSubtaskId, subtaskId);
        wrapper.orderByAsc(TechReformItem::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchUpdate(Long subtaskId, List<TechReformItem> items) {
        if (items == null || items.isEmpty()) {
            return 0;
        }
        // Validate: every item must belong to the given subtaskId
        for (TechReformItem item : items) {
            TechReformItem existing = this.getById(item.getId());
            if (existing == null) {
                throw new RuntimeException("治理项不存在: id=" + item.getId());
            }
            if (!subtaskId.equals(existing.getSubtaskId())) {
                throw new RuntimeException(
                    String.format("治理项 id=%d 不属于子任务 %d（实际属于 %d）",
                        item.getId(), subtaskId, existing.getSubtaskId()));
            }
            item.setSubtaskId(subtaskId);
        }
        this.updateBatchById(items);
        return items.size();
    }
}
