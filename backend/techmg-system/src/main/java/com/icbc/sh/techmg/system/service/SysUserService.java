package com.icbc.sh.techmg.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.icbc.sh.techmg.common.util.PageResult;
import com.icbc.sh.techmg.system.entity.SysUser;
import com.icbc.sh.techmg.system.dto.SysUserQueryDTO;
import com.icbc.sh.techmg.system.vo.SysRoleVO;
import com.icbc.sh.techmg.system.vo.SysUserVO;

import java.util.List;
import java.util.Map;

public interface SysUserService extends IService<SysUser> {
    SysUser getByUserId(String userId);
    PageResult<SysUserVO> pageUsers(SysUserQueryDTO dto);
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
    List<SysRoleVO> getRoles(Long userId);
}
