package com.icbc.sh.techmg.business.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.icbc.sh.techmg.business.dto.TechReformTaskCreateDTO;
import com.icbc.sh.techmg.business.dto.TechReformTaskUpdateDTO;
import com.icbc.sh.techmg.business.entity.TechReformTask;
import com.icbc.sh.techmg.business.vo.TechReformTaskVO;

public interface TechReformTaskService extends IService<TechReformTask> {
    IPage<TechReformTaskVO> pageTasks(Page<TechReformTask> page, String keyword, String category, String status);

    TechReformTaskVO getTaskVO(Long id);

    TechReformTaskVO saveTask(TechReformTaskCreateDTO dto);

    TechReformTaskVO updateTask(TechReformTaskUpdateDTO dto);

    void updateStatus(Long id, String newStatus);
}
