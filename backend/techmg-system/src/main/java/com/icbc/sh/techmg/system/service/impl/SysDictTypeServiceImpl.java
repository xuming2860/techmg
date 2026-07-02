package com.icbc.sh.techmg.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.icbc.sh.techmg.common.constant.ResultCode;
import com.icbc.sh.techmg.common.exception.BusinessException;
import com.icbc.sh.techmg.common.util.PageResult;
import com.icbc.sh.techmg.system.dto.SysDictTypeCreateDTO;
import com.icbc.sh.techmg.system.dto.SysDictTypeUpdateDTO;
import com.icbc.sh.techmg.system.entity.SysDictType;
import com.icbc.sh.techmg.system.mapper.SysDictTypeMapper;
import com.icbc.sh.techmg.system.service.SysDictTypeService;
import com.icbc.sh.techmg.system.vo.SysDictTypeVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SysDictTypeServiceImpl extends ServiceImpl<SysDictTypeMapper, SysDictType>
        implements SysDictTypeService {

    @Override
    public PageResult<SysDictTypeVO> pageDictTypes(IPage<SysDictType> page) {
        return PageResult.from(this.page(page), this::toVO);
    }

    @Override
    public SysDictTypeVO getDictTypeVO(Long id) {
        SysDictType entity = getById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "字典类型不存在");
        }
        return toVO(entity);
    }

    @Override
    public SysDictTypeVO saveDictType(SysDictTypeCreateDTO dto) {
        long count = count(new LambdaQueryWrapper<SysDictType>().eq(SysDictType::getDictType, dto.getDictType()));
        if (count > 0) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "字典类型已存在");
        }
        SysDictType entity = new SysDictType();
        BeanUtils.copyProperties(dto, entity);
        save(entity);
        return toVO(entity);
    }

    @Override
    public SysDictTypeVO updateDictType(SysDictTypeUpdateDTO dto) {
        SysDictType existing = getById(dto.getId());
        if (existing == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "字典类型不存在");
        }
        BeanUtils.copyProperties(dto, existing);
        updateById(existing);
        return toVO(existing);
    }

    @Override
    public void deleteDictType(Long id) {
        SysDictType entity = getById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "字典类型不存在");
        }
        removeById(id);
    }

    private SysDictTypeVO toVO(SysDictType entity) {
        SysDictTypeVO vo = new SysDictTypeVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
