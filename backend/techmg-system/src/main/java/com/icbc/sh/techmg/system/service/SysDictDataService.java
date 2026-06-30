package com.icbc.sh.techmg.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.icbc.sh.techmg.system.entity.SysDictData;

import java.util.List;

public interface SysDictDataService extends IService<SysDictData> {

    List<SysDictData> getByDictType(String dictType);
}
