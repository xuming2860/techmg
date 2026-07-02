package com.icbc.sh.techmg.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.icbc.sh.techmg.business.dto.TechReformSubtaskCreateDTO;
import com.icbc.sh.techmg.business.dto.TechReformSubtaskUpdateDTO;
import com.icbc.sh.techmg.business.entity.TechReformSubtask;
import com.icbc.sh.techmg.business.mapper.TechReformSubtaskMapper;
import com.icbc.sh.techmg.business.service.TechReformSubtaskService;
import com.icbc.sh.techmg.business.vo.TechReformSubtaskVO;
import com.icbc.sh.techmg.common.constant.ResultCode;
import com.icbc.sh.techmg.common.exception.BusinessException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class TechReformSubtaskServiceImpl extends ServiceImpl<TechReformSubtaskMapper, TechReformSubtask>
        implements TechReformSubtaskService {

    @Override
    public IPage<TechReformSubtaskVO> pageSubtasks(Page<TechReformSubtask> page, Long parentTaskId, String status, String keyword) {
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
        IPage<TechReformSubtask> result = this.page(page, wrapper);
        return result.convert(this::toVO);
    }

    @Override
    public TechReformSubtaskVO getSubtaskVO(Long id) {
        TechReformSubtask subtask = getById(id);
        if (subtask == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "子任务不存在");
        }
        return toVO(subtask);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TechReformSubtaskVO saveSubtask(TechReformSubtaskCreateDTO dto) {
        TechReformSubtask subtask = new TechReformSubtask();
        BeanUtils.copyProperties(dto, subtask);
        save(subtask);
        return toVO(subtask);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TechReformSubtaskVO updateSubtask(TechReformSubtaskUpdateDTO dto) {
        TechReformSubtask existing = getById(dto.getId());
        if (existing == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "子任务不存在");
        }
        BeanUtils.copyProperties(dto, existing);
        updateById(existing);
        return toVO(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, String newStatus) {
        if (newStatus == null || newStatus.isBlank()) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "状态值不能为空");
        }
        TechReformSubtask subtask = this.getById(id);
        if (subtask == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "子任务不存在");
        }
        subtask.setStatus(newStatus);
        this.updateById(subtask);
    }

    private TechReformSubtaskVO toVO(TechReformSubtask entity) {
        TechReformSubtaskVO vo = new TechReformSubtaskVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
