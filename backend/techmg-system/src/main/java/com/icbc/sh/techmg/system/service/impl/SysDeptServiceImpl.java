package com.icbc.sh.techmg.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.icbc.sh.techmg.common.constant.ResultCode;
import com.icbc.sh.techmg.common.exception.BusinessException;
import com.icbc.sh.techmg.common.util.TreeUtil;
import com.icbc.sh.techmg.system.dto.SysDeptCreateDTO;
import com.icbc.sh.techmg.system.dto.SysDeptUpdateDTO;
import com.icbc.sh.techmg.system.entity.SysDept;
import com.icbc.sh.techmg.system.mapper.SysDeptMapper;
import com.icbc.sh.techmg.system.service.SysDeptService;
import com.icbc.sh.techmg.system.vo.SysDeptVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements SysDeptService {

    @Override
    public List<SysDeptVO> getDeptTree() {
        List<SysDept> allDepts = this.list();
        if (allDepts.isEmpty()) {
            return Collections.emptyList();
        }
        allDepts.sort(Comparator.comparing(SysDept::getSort, Comparator.nullsLast(Comparator.naturalOrder())));
        List<SysDeptVO> voList = allDepts.stream().map(this::toVO).collect(Collectors.toList());
        return TreeUtil.buildTree(voList, SysDeptVO::getId, SysDeptVO::getParentId,
                SysDeptVO::setChildren, 0L);
    }

    @Override
    public SysDeptVO getDeptVO(Long id) {
        SysDept dept = getById(id);
        if (dept == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "部门不存在");
        }
        return toVO(dept);
    }

    @Override
    public SysDeptVO saveDept(SysDeptCreateDTO dto) {
        SysDept dept = new SysDept();
        BeanUtils.copyProperties(dto, dept);
        save(dept);
        return toVO(dept);
    }

    @Override
    public SysDeptVO updateDept(SysDeptUpdateDTO dto) {
        SysDept existing = getById(dto.getId());
        if (existing == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "部门不存在");
        }
        BeanUtils.copyProperties(dto, existing);
        updateById(existing);
        return toVO(existing);
    }

    @Override
    public void deleteDept(Long id) {
        SysDept dept = getById(id);
        if (dept == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "部门不存在");
        }
        long childCount = count(new LambdaQueryWrapper<SysDept>().eq(SysDept::getParentId, id));
        if (childCount > 0) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "存在子部门，无法删除");
        }
        removeById(id);
    }

    private SysDeptVO toVO(SysDept entity) {
        SysDeptVO vo = new SysDeptVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
