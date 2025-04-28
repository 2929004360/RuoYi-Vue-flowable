package com.ruoyi.common.enums;

/**
 * 流程菜单
 *
 * @author fengcheng
 */

public enum FlowMenuEnum {
    RISK_FLOW_MENU("隐患菜单", "2", "2064,2078"),
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
