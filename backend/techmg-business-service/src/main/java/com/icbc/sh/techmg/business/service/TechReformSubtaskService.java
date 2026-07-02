package com.icbc.sh.techmg.business.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.icbc.sh.techmg.business.dto.TechReformSubtaskCreateDTO;
import com.icbc.sh.techmg.business.dto.TechReformSubtaskUpdateDTO;
import com.icbc.sh.techmg.business.entity.TechReformSubtask;
import com.icbc.sh.techmg.business.vo.TechReformSubtaskVO;

public interface TechReformSubtaskService extends IService<TechReformSubtask> {
    IPage<TechReformSubtaskVO> pageSubtasks(Page<TechReformSubtask> page, Long parentTaskId, String status, String keyword);

    TechReformSubtaskVO getSubtaskVO(Long id);

    TechReformSubtaskVO saveSubtask(TechReformSubtaskCreateDTO dto);

    TechReformSubtaskVO updateSubtask(TechReformSubtaskUpdateDTO dto);

    void updateStatus(Long id, String newStatus);
}
