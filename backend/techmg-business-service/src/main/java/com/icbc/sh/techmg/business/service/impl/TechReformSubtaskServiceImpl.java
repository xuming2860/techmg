package com.icbc.sh.techmg.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.icbc.sh.techmg.business.entity.TechReformSubtask;
import com.icbc.sh.techmg.business.mapper.TechReformSubtaskMapper;
import com.icbc.sh.techmg.business.service.TechReformSubtaskService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TechReformSubtaskServiceImpl extends ServiceImpl<TechReformSubtaskMapper, TechReformSubtask>
        implements TechReformSubtaskService {

    @Override
    public IPage<TechReformSubtask> pageSubtasks(Page<TechReformSubtask> page, Long parentTaskId, String status, String keyword) {
        LambdaQueryWrapper<TechReformSubtask> wrapper = new LambdaQueryWrapper<>();
        if (parentTaskId != null) {
            wrapper.eq(TechReformSubtask::getParentTaskId, parentTaskId);
        }
        if (status != null && !status.isBlank()) {
            wrapper.eq(TechReformSubtask::getStatus, status);
        }
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(TechReformSubtask::getSubtaskName, keyword);
        }
        wrapper.orderByDesc(TechReformSubtask::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, String newStatus) {
        TechReformSubtask subtask = this.getById(id);
        if (subtask != null) {
            subtask.setStatus(newStatus);
            this.updateById(subtask);
        }
    }
}
