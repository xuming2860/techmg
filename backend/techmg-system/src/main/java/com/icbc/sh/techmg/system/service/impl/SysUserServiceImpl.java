package com.icbc.sh.techmg.system.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.icbc.sh.techmg.system.entity.SysUser;
import com.icbc.sh.techmg.system.entity.SysUserRole;
import com.icbc.sh.techmg.system.mapper.SysUserMapper;
import com.icbc.sh.techmg.system.mapper.SysUserRoleMapper;
import com.icbc.sh.techmg.system.model.dto.UserQueryDTO;
import com.icbc.sh.techmg.system.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final SysUserRoleMapper sysUserRoleMapper;

    @Override
    @DS("slave")
    public SysUser getByAuthNo(String authNo) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getAuthNo, authNo);
        return this.getOne(wrapper);
    }

    @Override
    @DS("slave")
    public IPage<SysUser> pageUsers(UserQueryDTO dto) {
        Page<SysUser> page = new Page<>(
                dto.getPage() != null ? dto.getPage() : 1,
                dto.getSize() != null ? dto.getSize() : 10
        );
        return this.baseMapper.selectUserPage(page, dto);
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
}
