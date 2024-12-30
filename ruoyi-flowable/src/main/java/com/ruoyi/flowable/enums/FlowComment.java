package com.ruoyi.flowable.enums;

/**
 * 流程意见类型
 *
 * @author fengcheng
 * @date 2023-09-25
 */
public enum FlowComment {

    /**
     * 说明
     */
    NORMAL("1", "正常意见"),
    REBACK("2", "退回意见"),
    REJECT("3", "驳回意见"),
    DELEGATE("4", "委派意见"),
    TRANSFER("5", "转办意见"),
    STOP("6", "取消流程"),
    REVOKE("7","撤回意见"),
    REFUSE("8","拒绝意见"),
    SKIP("9","跳过流程"),
    QJQ("10","前加签"),
    HJQ("11","后加签"),
    DSLJQ("12","多实例加签"),
    JUMP("13","跳转意见"),
    RECALL("14","收回意见");

    /**
     * 类型
     */
    private final String type;

    /**
     * 说明
     */
    private final String remark;

    FlowComment(String type, String remark) {
        this.type = type;
        this.remark = remark;
    }

    public String getType() {
        return type;
    }

    public String getRemark() {
        return remark;
    }
}
