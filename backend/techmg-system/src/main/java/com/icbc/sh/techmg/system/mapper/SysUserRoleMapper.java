package com.icbc.sh.techmg.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.icbc.sh.techmg.system.entity.SysRole;
import com.icbc.sh.techmg.system.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    /**
     * Query roles assigned to a user by joining sys_user_role and sys_role.
     */
    List<SysRole> selectRolesByUserId(@Param("userId") Long userId);
}
