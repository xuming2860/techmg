package com.icbc.sh.techmg.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.icbc.sh.techmg.system.entity.SysDept;

import java.util.List;

public interface SysDeptService extends IService<SysDept> {
    List<SysDept> getDeptTree();
}
