package com.ruoyi.common.enums;


import com.ruoyi.common.utils.StringUtils;

/**
 * @author fengcheng
 * @since 2023/3/9 00:45
 */
public enum ProcessStatus {

    /**
     * 未审批
     */
    UNAPPROVED("unapproved"),

    /**
     * 进行中（审批中）
     */
    RUNNING("running"),
    /**
     * 已终止
     */
    TERMINATED("terminated"),
    /**
     * 已完成
     */
    COMPLETED("completed"),
    /**
     * 已取消
     */
    CANCELED("canceled");

    private final String status;

    public static ProcessStatus getProcessStatus(String str) {
        if (StringUtils.isNotBlank(str)) {
            for (ProcessStatus value : values()) {
                if (StringUtils.equalsIgnoreCase(str, value.getStatus())) {
                    return value;
                }
            }
        }
        return null;
    }

    public String getStatus() {
        return status;
    }

    ProcessStatus(String status) {
        this.status = status;
    }
}
