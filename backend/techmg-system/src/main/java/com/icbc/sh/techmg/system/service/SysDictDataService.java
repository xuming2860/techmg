package com.icbc.sh.techmg.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.icbc.sh.techmg.system.dto.SysDictDataCreateDTO;
import com.icbc.sh.techmg.system.dto.SysDictDataUpdateDTO;
import com.icbc.sh.techmg.system.entity.SysDictData;
import com.icbc.sh.techmg.system.vo.SysDictDataVO;

import java.util.List;

public interface SysDictDataService extends IService<SysDictData> {

    List<SysDictDataVO> getByDictType(String dictType);

    SysDictDataVO getDictDataVO(Long id);

    SysDictDataVO saveDictData(SysDictDataCreateDTO dto);

    SysDictDataVO updateDictData(SysDictDataUpdateDTO dto);

    /** 删除字典数据，业务逻辑在 Service 层闭环 */
    void deleteDictData(Long id);
}
