package com.icbc.sh.techmg.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.icbc.sh.techmg.business.entity.TechReformTask;
import com.icbc.sh.techmg.business.mapper.TechReformTaskMapper;
import com.icbc.sh.techmg.business.service.TechReformTaskService;
import org.springframework.stereotype.Service;

@Service
public class TechReformTaskServiceImpl extends ServiceImpl<TechReformTaskMapper, TechReformTask>
        implements TechReformTaskService {

    @Override
    public IPage<TechReformTask> pageTasks(Page<TechReformTask> page, String keyword, String category, String status) {
        LambdaQueryWrapper<TechReformTask> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(TechReformTask::getTaskName, keyword);
        }
        if (category != null && !category.isBlank()) {
            wrapper.eq(TechReformTask::getTaskCategory, category);
        }
        if (status != null && !status.isBlank()) {
            wrapper.eq(TechReformTask::getStatus, status);
        }
        wrapper.orderByDesc(TechReformTask::getCreateTime);
        return this.page(page, wrapper);
    }
}
