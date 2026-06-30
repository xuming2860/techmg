package com.icbc.sh.techmg.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.icbc.sh.techmg.system.entity.SysUser;
import com.icbc.sh.techmg.system.model.dto.UserQueryDTO;

import java.util.List;

public interface SysUserService extends IService<SysUser> {
    SysUser getByAuthNo(String authNo);
    IPage<SysUser> pageUsers(UserQueryDTO dto);
    void assignRoles(Long userId, List<Long> roleIds);
}
