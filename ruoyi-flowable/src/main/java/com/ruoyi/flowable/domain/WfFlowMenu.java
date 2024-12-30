package com.ruoyi.flowable.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.flowable.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 流程菜单对象 wf_flow_menu
 *
 * @author fengcheng
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wf_flow_menu" )
public class WfFlowMenu extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 流程菜单id
     */
    private Long flowMenuId;

    /**
     * 菜单ID
     */
    @Excel(name = "菜单ID" )
    private String menuId;

    /**
     * 菜单名称
     */
    @Excel(name = "菜单名称" )
    private String name;

    /**
     * 表单提交路由
     */
    @Excel(name = "表单提交路由" )
    private String create;

    /**
     * 表单查看路由
     */
    @Excel(name = "表单查看路由" )
    private String view;
}
