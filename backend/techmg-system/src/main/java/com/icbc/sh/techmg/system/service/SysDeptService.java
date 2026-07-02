package com.icbc.sh.techmg.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.icbc.sh.techmg.system.dto.SysDeptCreateDTO;
import com.icbc.sh.techmg.system.dto.SysDeptUpdateDTO;
import com.icbc.sh.techmg.system.entity.SysDept;
import com.icbc.sh.techmg.system.vo.SysDeptVO;

import java.util.List;

public interface SysDeptService extends IService<SysDept> {
    List<SysDeptVO> getDeptTree();

    SysDeptVO getDeptVO(Long id);

    SysDeptVO saveDept(SysDeptCreateDTO dto);

    SysDeptVO updateDept(SysDeptUpdateDTO dto);

    /** 删除部门（含子部门校验），业务逻辑在 Service 层闭环 */
    void deleteDept(Long id);
}
