package com.icbc.sh.techmg.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.icbc.sh.techmg.common.util.PageResult;
import com.icbc.sh.techmg.system.dto.SysDictTypeCreateDTO;
import com.icbc.sh.techmg.system.dto.SysDictTypeUpdateDTO;
import com.icbc.sh.techmg.system.entity.SysDictType;
import com.icbc.sh.techmg.system.vo.SysDictTypeVO;

public interface SysDictTypeService extends IService<SysDictType> {

    /** 分页查询字典类型（返回 VO，不暴露 Entity） */
    PageResult<SysDictTypeVO> pageDictTypes(IPage<SysDictType> page);

    SysDictTypeVO getDictTypeVO(Long id);

    SysDictTypeVO saveDictType(SysDictTypeCreateDTO dto);

    SysDictTypeVO updateDictType(SysDictTypeUpdateDTO dto);

    /** 删除字典类型（含唯一性校验），业务逻辑在 Service 层闭环 */
    void deleteDictType(Long id);
}
