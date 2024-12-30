package com.ruoyi.system.domain.vo;

import com.ruoyi.common.core.domain.entity.SysMenu;

import java.util.List;

/**
 * 菜单权限表 sys_menu
 *
 * @author ruoyi
 */
public class SysMenuVo extends SysMenu {

    /**
     * 菜单数据
     */
    private List<SysMenuVo> menuVoList;

    public List<SysMenuVo> getMenuList() {
        return menuVoList;
    }

    public void setMenuList(List<SysMenuVo> menuList) {
        this.menuVoList = menuList;
    }

    @Override
    public String toString() {
        return "SysMenuVo{" +
                "menuList=" + menuVoList +
                '}';
    }
}
