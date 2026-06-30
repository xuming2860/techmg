package com.icbc.sh.techmg.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.icbc.sh.techmg.system.entity.SysDept;
import com.icbc.sh.techmg.system.mapper.SysDeptMapper;
import com.icbc.sh.techmg.system.service.SysDeptService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements SysDeptService {

    @Override
    public List<SysDept> getDeptTree() {
        List<SysDept> allDepts = this.list();
        if (allDepts.isEmpty()) {
            return Collections.emptyList();
        }
        allDepts.sort(Comparator.comparing(SysDept::getSort, Comparator.nullsLast(Comparator.naturalOrder())));
        return buildTree(allDepts, 0L);
    }

    private List<SysDept> buildTree(List<SysDept> allDepts, Long parentId) {
        return allDepts.stream()
                .filter(d -> d.getParentId() != null && d.getParentId().equals(parentId))
                .peek(d -> d.setChildren(buildTree(allDepts, d.getId())))
                .collect(Collectors.toList());
    }
}
