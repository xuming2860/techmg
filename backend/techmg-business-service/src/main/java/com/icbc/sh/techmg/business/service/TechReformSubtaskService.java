package com.icbc.sh.techmg.business.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.icbc.sh.techmg.business.entity.TechReformSubtask;

public interface TechReformSubtaskService extends IService<TechReformSubtask> {
    IPage<TechReformSubtask> pageSubtasks(Page<TechReformSubtask> page, Long parentTaskId, String status, String keyword);

    void updateStatus(Long id, String newStatus);
}
