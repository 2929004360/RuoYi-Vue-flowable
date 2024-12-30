package com.ruoyi.flowable.enums;

/**
 * 权限枚举
 *
 * @author fengcheng
 */
public enum PermissionEnum {
    USER_PERMISSION("人员权限", "1"),
    DEPT_PERMISSION("部门权限", "2"),
    POST_PERMISSION("岗位权限", "3"),
    POSITION_PERMISSION("职位权限", "4"),
    ROLE_PERMISSION("角色权限", "5"),
    ;

    private final String name;

    private final String code;

    PermissionEnum(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }
}
