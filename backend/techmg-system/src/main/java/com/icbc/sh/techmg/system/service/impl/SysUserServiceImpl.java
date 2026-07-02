package com.icbc.sh.techmg.system.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.icbc.sh.techmg.common.util.PageResult;
import com.icbc.sh.techmg.system.entity.SysRole;
import com.icbc.sh.techmg.system.entity.SysUser;
import com.icbc.sh.techmg.system.entity.SysUserBranch;
import com.icbc.sh.techmg.system.entity.SysUserRole;
import com.icbc.sh.techmg.system.mapper.SysUserBranchMapper;
import com.icbc.sh.techmg.system.mapper.SysUserMapper;
import com.icbc.sh.techmg.system.mapper.SysUserRoleMapper;
import com.icbc.sh.techmg.system.mapper.SysRoleMapper;
import com.icbc.sh.techmg.system.dto.SysUserQueryDTO;
import com.icbc.sh.techmg.system.service.SysUserService;
import com.icbc.sh.techmg.system.vo.SysRoleVO;
import com.icbc.sh.techmg.system.vo.SysUserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysUserBranchMapper sysUserBranchMapper;
    private final SysRoleMapper sysRoleMapper;

    @Override
    @DS("slave")
    public SysUser getByAuthNo(String authNo) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getAuthNo, authNo);
        return this.getOne(wrapper);
    }

    @Override
    @DS("slave")
    public PageResult<SysUserVO> pageUsers(SysUserQueryDTO dto) {
        Page<SysUser> page = new Page<>(
                dto.getPage() != null ? dto.getPage() : 1,
                dto.getSize() != null ? dto.getSize() : 10
        );
        IPage<SysUser> result = this.baseMapper.selectUserPage(page, dto);
        return PageResult.from(result, this::toVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(Long userId, List<Long> roleIds) {
        // clear existing
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, userId);
        sysUserRoleMapper.delete(wrapper);

        // insert new
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                sysUserRoleMapper.insert(ur);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysUser syncUserInfo(Map<String, Object> info) {
        String authNo = (String) info.get("authNo");
        SysUser user = getByAuthNo(authNo);

        if (user == null) {
            // First login — create user, save first to get id, then assign default role
            user = new SysUser();
            user.setAuthNo(authNo);
            user.setUsername(authNo);
            user.setStatus(1); // enabled
            this.save(user);

            // Assign default GUEST role (query by code, not hardcoded id)
            SysRole guestRole = sysRoleMapper.selectOne(
                new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleCode, "GUEST"));
            if (guestRole != null) {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(user.getId());
                ur.setRoleId(guestRole.getId());
                sysUserRoleMapper.insert(ur);
            }
        }

        // Update fields from SSIC/AAM API
        user.setRealName((String) info.getOrDefault("tellername", ""));
        user.setAdAccount((String) info.getOrDefault("ad", ""));
        user.setBranchId((String) info.getOrDefault("branchId", ""));
        user.setBranchName((String) info.getOrDefault("branchName", ""));
        user.setNotesId((String) info.getOrDefault("notesId", ""));
        user.setLastLoginTime(java.time.LocalDateTime.now());

        this.saveOrUpdate(user);

        // Sync branch list
        @SuppressWarnings("unchecked")
        List<Map<String, String>> branchList = (List<Map<String, String>>) info.get("branchIdList");
        if (branchList != null) {
            LambdaQueryWrapper<SysUserBranch> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysUserBranch::getUserId, user.getId());
            sysUserBranchMapper.delete(wrapper);

            for (Map<String, String> branch : branchList) {
                SysUserBranch ub = new SysUserBranch();
                ub.setUserId(user.getId());
                ub.setBranchId(branch.get("branchId"));
                ub.setBranchName(branch.get("branchName"));
                sysUserBranchMapper.insert(ub);
            }
        }

        return user;
    }

    @Override
    public List<SysRoleVO> getRoles(Long userId) {
        return sysUserRoleMapper.selectRolesByUserId(userId).stream()
                .map(role -> {
                    SysRoleVO vo = new SysRoleVO();
                    BeanUtils.copyProperties(role, vo);
                    return vo;
                })
                .collect(Collectors.toList());
    }

    private SysUserVO toVO(SysUser entity) {
        SysUserVO vo = new SysUserVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
