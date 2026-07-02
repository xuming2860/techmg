package com.icbc.sh.techmg.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.icbc.sh.techmg.system.dto.SysMenuCreateDTO;
import com.icbc.sh.techmg.system.dto.SysMenuUpdateDTO;
import com.icbc.sh.techmg.system.entity.SysMenu;
import com.icbc.sh.techmg.system.vo.SysMenuVO;

import java.util.List;

public interface SysMenuService extends IService<SysMenu> {
    List<SysMenuVO> getMenuTree();
    List<SysMenuVO> getMenusByRoleId(Long roleId);
    List<SysMenuVO> getMenuTreeByUserId(Long userId);

    SysMenuVO getMenuVO(Long id);

    SysMenuVO saveMenu(SysMenuCreateDTO dto);

    SysMenuVO updateMenu(SysMenuUpdateDTO dto);

    /** 删除菜单（含子菜单校验），业务逻辑在 Service 层闭环 */
    void deleteMenu(Long id);
}
