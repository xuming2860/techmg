package com.icbc.sh.techmg.business.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.icbc.sh.techmg.business.dto.TechReformItemCreateDTO;
import com.icbc.sh.techmg.business.dto.TechReformItemUpdateDTO;
import com.icbc.sh.techmg.business.entity.TechReformItem;
import com.icbc.sh.techmg.business.vo.TechReformItemVO;

import java.util.List;

public interface TechReformItemService extends IService<TechReformItem> {
    IPage<TechReformItemVO> pageItems(Page<TechReformItem> page, Long subtaskId, String appName, String status, String keyword);

    TechReformItemVO getItemVO(Long id);

    TechReformItemVO saveItem(TechReformItemCreateDTO dto);

    TechReformItemVO updateItem(TechReformItemUpdateDTO dto);

    int importItems(Long subtaskId, String mode, List<TechReformItem> items);

    List<TechReformItem> exportItems(Long subtaskId);

    int batchUpdate(Long subtaskId, List<TechReformItem> items);
}
