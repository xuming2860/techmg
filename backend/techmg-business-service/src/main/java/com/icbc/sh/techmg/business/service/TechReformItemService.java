package com.icbc.sh.techmg.business.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.icbc.sh.techmg.business.entity.TechReformItem;

import java.util.List;

public interface TechReformItemService extends IService<TechReformItem> {
    IPage<TechReformItem> pageItems(Page<TechReformItem> page, Long subtaskId, String appName, String status);

    int importItems(Long subtaskId, String mode, List<TechReformItem> items);

    List<TechReformItem> exportItems(Long subtaskId);

    int batchUpdate(List<TechReformItem> items);
}
