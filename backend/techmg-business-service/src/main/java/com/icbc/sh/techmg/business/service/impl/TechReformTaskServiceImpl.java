package com.icbc.sh.techmg.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.icbc.sh.techmg.business.dto.TechReformTaskCreateDTO;
import com.icbc.sh.techmg.business.dto.TechReformTaskUpdateDTO;
import com.icbc.sh.techmg.business.entity.TechReformTask;
import com.icbc.sh.techmg.business.mapper.TechReformTaskMapper;
import com.icbc.sh.techmg.business.service.TechReformTaskService;
import com.icbc.sh.techmg.business.vo.TechReformTaskVO;
import com.icbc.sh.techmg.common.constant.ResultCode;
import com.icbc.sh.techmg.common.enums.ReformStatus;
import com.icbc.sh.techmg.common.exception.BusinessException;
import com.icbc.sh.techmg.system.entity.SysUser;
import com.icbc.sh.techmg.system.service.SysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TechReformTaskServiceImpl extends ServiceImpl<TechReformTaskMapper, TechReformTask>
        implements TechReformTaskService {

    private final SysUserService sysUserService;

    @Override
    public IPage<TechReformTaskVO> pageTasks(Page<TechReformTask> page, String keyword, String category, String status) {
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
        IPage<TechReformTask> result = this.page(page, wrapper);
        return result.convert(this::toVO);
    }

    @Override
    public TechReformTaskVO getTaskVO(Long id) {
        TechReformTask task = getById(id);
        if (task == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "任务不存在");
        }
        return toVO(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TechReformTaskVO saveTask(TechReformTaskCreateDTO dto) {
        TechReformTask task = new TechReformTask();
        BeanUtils.copyProperties(dto, task);
        // 新建任务默认状态为“待开始”
        if (task.getStatus() == null || task.getStatus().isBlank()) {
            task.setStatus(ReformStatus.PENDING.name());
        }
        // Set taskOwner from current authenticated user if not provided
        if (task.getTaskOwner() == null || task.getTaskOwner().isBlank()) {
            String ownerName = getCurrentUserName();
            if (ownerName != null) {
                task.setTaskOwner(ownerName);
            }
        }
        save(task);
        return toVO(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TechReformTaskVO updateTask(TechReformTaskUpdateDTO dto) {
        TechReformTask existing = getById(dto.getId());
        if (existing == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "任务不存在");
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
        TechReformTask task = this.getById(id);
        if (task == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "任务不存在");
        }
        task.setStatus(newStatus);
        this.updateById(task);
    }

    private String getCurrentUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof UserDetails userDetails) {
            String authNo = userDetails.getUsername();
            SysUser sysUser = sysUserService.getByAuthNo(authNo);
            if (sysUser != null) {
                return sysUser.getRealName() != null ? sysUser.getRealName() : authNo;
            }
            return authNo;
        }
        return null;
    }

    private TechReformTaskVO toVO(TechReformTask entity) {
        TechReformTaskVO vo = new TechReformTaskVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
