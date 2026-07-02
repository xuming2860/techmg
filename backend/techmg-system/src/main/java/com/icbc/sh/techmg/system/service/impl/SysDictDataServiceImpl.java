package com.icbc.sh.techmg.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.icbc.sh.techmg.common.constant.ResultCode;
import com.icbc.sh.techmg.common.exception.BusinessException;
import com.icbc.sh.techmg.system.dto.SysDictDataCreateDTO;
import com.icbc.sh.techmg.system.dto.SysDictDataUpdateDTO;
import com.icbc.sh.techmg.system.entity.SysDictData;
import com.icbc.sh.techmg.system.mapper.SysDictDataMapper;
import com.icbc.sh.techmg.system.service.SysDictDataService;
import com.icbc.sh.techmg.system.vo.SysDictDataVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SysDictDataServiceImpl extends ServiceImpl<SysDictDataMapper, SysDictData>
        implements SysDictDataService {

    @Override
    public List<SysDictDataVO> getByDictType(String dictType) {
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictData::getDictType, dictType)
                .orderByAsc(SysDictData::getSort);
        List<SysDictData> list = this.list(wrapper);
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public SysDictDataVO getDictDataVO(Long id) {
        SysDictData entity = getById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "字典数据不存在");
        }
        return toVO(entity);
    }

    @Override
    public SysDictDataVO saveDictData(SysDictDataCreateDTO dto) {
        SysDictData entity = new SysDictData();
        BeanUtils.copyProperties(dto, entity);
        save(entity);
        return toVO(entity);
    }

    @Override
    public SysDictDataVO updateDictData(SysDictDataUpdateDTO dto) {
        SysDictData existing = getById(dto.getId());
        if (existing == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "字典数据不存在");
        }
        BeanUtils.copyProperties(dto, existing);
        updateById(existing);
        return toVO(existing);
    }

    @Override
    public void deleteDictData(Long id) {
        SysDictData entity = getById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "字典数据不存在");
        }
        removeById(id);
    }

    private SysDictDataVO toVO(SysDictData entity) {
        SysDictDataVO vo = new SysDictDataVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
