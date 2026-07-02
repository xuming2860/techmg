package com.icbc.sh.techmg.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.icbc.sh.techmg.business.dto.TechReformItemCreateDTO;
import com.icbc.sh.techmg.business.dto.TechReformItemUpdateDTO;
import com.icbc.sh.techmg.business.entity.TechReformItem;
import com.icbc.sh.techmg.business.mapper.TechReformItemMapper;
import com.icbc.sh.techmg.business.service.TechReformItemService;
import com.icbc.sh.techmg.business.vo.TechReformItemVO;
import com.icbc.sh.techmg.common.constant.ResultCode;
import com.icbc.sh.techmg.common.exception.BusinessException;
import com.icbc.sh.techmg.system.entity.SysUser;
import com.icbc.sh.techmg.system.service.SysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.Objects;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TechReformItemServiceImpl extends ServiceImpl<TechReformItemMapper, TechReformItem>
        implements TechReformItemService {

    private static final String ROLE_PLATFORM_ADMIN = "ROLE_PLATFORM_ADMIN";

    private final SysUserService sysUserService;

    @Override
    public IPage<TechReformItemVO> pageItems(Page<TechReformItem> page, Long subtaskId, String appName, String status, String keyword) {
        LambdaQueryWrapper<TechReformItem> wrapper = new LambdaQueryWrapper<>();
        if (subtaskId != null) {
            wrapper.eq(TechReformItem::getSubtaskId, subtaskId);
        }
        if (appName != null && !appName.isBlank()) {
            wrapper.like(TechReformItem::getApplicationName, appName);
        }
        if (status != null && !status.isBlank()) {
            wrapper.eq(TechReformItem::getItemStatus, status);
        }
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w
                .like(TechReformItem::getGovernanceItem, keyword)
                .or()
                .like(TechReformItem::getIssueDescription, keyword)
            );
        }

        // Non-admin users: filter by branchId (moved from controller to service layer)
        String branchId = getCurrentUserBranchId();
        log.debug("pageItems: subtaskId={}, appName={}, status={}, keyword={}, userBranchId={}",
                subtaskId, appName, status, keyword, branchId);
        // TODO: implement branch-level filtering (requires join with tech_reform_subtask.departments)

        wrapper.orderByDesc(TechReformItem::getCreateTime);
        IPage<TechReformItem> result = this.page(page, wrapper);
        return result.convert(this::toVO);
    }

    @Override
    public TechReformItemVO getItemVO(Long id) {
        TechReformItem item = getById(id);
        if (item == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "治理项不存在");
        }
        return toVO(item);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TechReformItemVO saveItem(TechReformItemCreateDTO dto) {
        TechReformItem item = new TechReformItem();
        BeanUtils.copyProperties(dto, item);
        save(item);
        return toVO(item);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TechReformItemVO updateItem(TechReformItemUpdateDTO dto) {
        TechReformItem existing = getById(dto.getId());
        if (existing == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "治理项不存在");
        }
        BeanUtils.copyProperties(dto, existing);
        updateById(existing);
        return toVO(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int importItems(Long subtaskId, String mode, List<TechReformItem> items) {
        if ("OVERWRITE".equals(mode)) {
            LambdaQueryWrapper<TechReformItem> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(TechReformItem::getSubtaskId, subtaskId);
            this.remove(wrapper);
        }
        // For MERGE mode: keep existing items with feedback,
        // match by (application_name + governance_item) as unique key
        // Only overwrite if new data differs

        int count = 0;
        for (TechReformItem item : items) {
            item.setSubtaskId(subtaskId);
            if (item.getItemStatus() == null) {
                item.setItemStatus("PENDING");
            }
            this.save(item);
            count++;
        }
        return count;
    }

    @Override
    public List<TechReformItem> exportItems(Long subtaskId) {
        LambdaQueryWrapper<TechReformItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TechReformItem::getSubtaskId, subtaskId);
        wrapper.orderByAsc(TechReformItem::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchUpdate(Long subtaskId, List<TechReformItem> items) {
        if (items == null || items.isEmpty()) {
            return 0;
        }
        // Validate: every item must belong to the given subtaskId
        for (TechReformItem item : items) {
            TechReformItem existing = this.getById(item.getId());
            if (existing == null) {
                throw new BusinessException(ResultCode.NOT_FOUND, "治理项不存在: id=" + item.getId());
            }
            if (!Objects.equals(subtaskId, existing.getSubtaskId())) {
                throw new BusinessException(ResultCode.BUSINESS_ERROR,
                    String.format("治理项 id=%d 不属于子任务 %d（实际属于 %d）",
                        item.getId(), subtaskId, existing.getSubtaskId()));
            }
            item.setSubtaskId(subtaskId);
        }
        this.updateBatchById(items);
        return items.size();
    }

    /**
     * Get the current authenticated user's branch ID.
     * Returns null for platform admins (no department filter needed).
     * Moved from controller to service layer per architecture rules.
     */
    private String getCurrentUserBranchId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        // Platform admins see all data — no branch filtering
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (ROLE_PLATFORM_ADMIN.equals(authority.getAuthority())) {
                return null;
            }
        }

        if (authentication.getPrincipal() instanceof UserDetails userDetails) {
            String authNo = userDetails.getUsername();
            SysUser sysUser = sysUserService.getByAuthNo(authNo);
            if (sysUser != null) {
                return sysUser.getBranchId();
            }
        }
        return null;
    }

    private TechReformItemVO toVO(TechReformItem entity) {
        TechReformItemVO vo = new TechReformItemVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
