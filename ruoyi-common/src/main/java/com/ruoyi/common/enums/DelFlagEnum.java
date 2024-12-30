package com.ruoyi.common.enums;

/**
 * 删除状态
 *
 * @author fengcheng
 */
public enum DelFlagEnum {

    EXISTENCE("代表存在", "0"),
    DELETE("代表删除", "1");


    private final String name;

    private final String code;

    DelFlagEnum(String name, String code) {
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
