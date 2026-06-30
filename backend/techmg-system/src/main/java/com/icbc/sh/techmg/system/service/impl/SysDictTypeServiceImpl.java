package com.icbc.sh.techmg.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.icbc.sh.techmg.system.entity.SysDictType;
import com.icbc.sh.techmg.system.mapper.SysDictTypeMapper;
import com.icbc.sh.techmg.system.service.SysDictTypeService;
import org.springframework.stereotype.Service;

@Service
public class SysDictTypeServiceImpl extends ServiceImpl<SysDictTypeMapper, SysDictType>
        implements SysDictTypeService {
}
