package com.icbc.sh.techmg.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.icbc.sh.techmg.system.entity.SysUser;

public interface SysUserService extends IService<SysUser> {
    SysUser getByAuthNo(String authNo);
}
