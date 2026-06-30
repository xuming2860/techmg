package com.icbc.sh.techmg.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.icbc.sh.techmg.system.entity.SysDictData;
import com.icbc.sh.techmg.system.mapper.SysDictDataMapper;
import com.icbc.sh.techmg.system.service.SysDictDataService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysDictDataServiceImpl extends ServiceImpl<SysDictDataMapper, SysDictData>
        implements SysDictDataService {

    @Override
    @Cacheable(value = "dict", key = "#dictType")
    public List<SysDictData> getByDictType(String dictType) {
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictData::getDictType, dictType)
                .orderByAsc(SysDictData::getSort);
        return this.list(wrapper);
    }

    @Override
    @CacheEvict(value = "dict", allEntries = true)
    public boolean save(SysDictData entity) {
        return super.save(entity);
    }

    @Override
    @CacheEvict(value = "dict", allEntries = true)
    public boolean updateById(SysDictData entity) {
        return super.updateById(entity);
    }

    @Override
    @CacheEvict(value = "dict", allEntries = true)
    public boolean removeById(java.io.Serializable id) {
        return super.removeById(id);
    }
}
