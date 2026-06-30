package com.icbc.sh.techmg.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.icbc.sh.techmg.system.entity.SysRole;
import com.icbc.sh.techmg.system.entity.SysUser;
import com.icbc.sh.techmg.system.model.dto.UserQueryDTO;

import java.util.List;
import java.util.Map;

public interface SysUserService extends IService<SysUser> {
    SysUser getByAuthNo(String authNo);
    IPage<SysUser> pageUsers(UserQueryDTO dto);
    void assignRoles(Long userId, List<Long> roleIds);

    /**
     * Sync user info from external API. Upsert logic:
     * - If authNo not in sys_user, create with default role
     * - If exists, update fields and last_login_time
     * Also sync sys_user_branch records.
     */
    SysUser syncUserInfo(Map<String, Object> userInfoMap);

    /**
     * Get roles assigned to a user.
     */
    List<SysRole> getRoles(Long userId);
}
