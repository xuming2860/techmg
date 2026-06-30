package com.icbc.sh.techmg.business.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.icbc.sh.techmg.business.entity.TechReformTask;

public interface TechReformTaskService extends IService<TechReformTask> {
    IPage<TechReformTask> pageTasks(Page<TechReformTask> page, String keyword, String category, String status);

    void updateStatus(Long id, String newStatus);
}
