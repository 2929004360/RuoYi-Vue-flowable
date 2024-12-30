package com.ruoyi.common.enums;

/**
 * 流程菜单
 *
 * @author fengcheng
 */

public enum FlowMenuEnum {
    LEAVE_FLOW_MENU("请假菜单", "1", "2064,2065"),
    ;

    private final String name;

    private final String code;

    private final String menuId;

    FlowMenuEnum(String name, String code, String menuId) {
        this.name = name;
        this.code = code;
        this.menuId = menuId;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getMenuId() {
        return menuId;
    }
}
