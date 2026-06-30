package com.icbc.sh.techmg.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.icbc.sh.techmg.system.entity.SysUser;
import com.icbc.sh.techmg.system.mapper.SysUserMapper;
import com.icbc.sh.techmg.system.service.SysUserService;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Override
    public SysUser getByAuthNo(String authNo) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getAuthNo, authNo);
        return this.getOne(wrapper);
    }
}
