package com.icbc.sh.techmg.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.icbc.sh.techmg.common.util.PageResult;
import com.icbc.sh.techmg.system.dto.SysRoleCreateDTO;
import com.icbc.sh.techmg.system.dto.SysRoleUpdateDTO;
import com.icbc.sh.techmg.system.entity.SysRole;
import com.icbc.sh.techmg.system.vo.SysRoleVO;

import java.util.List;

public interface SysRoleService extends IService<SysRole> {
    /** 分页查询角色（返回 VO，不暴露 Entity） */
    PageResult<SysRoleVO> pageRoles(IPage<SysRole> page);

    List<SysRoleVO> getRolesByUserId(Long userId);

    SysRoleVO getRoleVO(Long id);

    SysRoleVO saveRole(SysRoleCreateDTO dto);

    SysRoleVO updateRole(SysRoleUpdateDTO dto);

    List<Long> getMenuIdsByRoleId(Long roleId);

    void assignMenus(Long roleId, List<Long> menuIds);
}
