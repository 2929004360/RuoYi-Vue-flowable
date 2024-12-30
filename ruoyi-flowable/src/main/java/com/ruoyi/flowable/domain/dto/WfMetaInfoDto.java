package com.ruoyi.flowable.domain.dto;

import lombok.Data;

/**
 * @author fengcheng
 * @createTime 2022/6/21 9:16
 */
@Data
public class WfMetaInfoDto {

    /**
     * 创建者（username）
     */
    private String createUser;

    /**
     * 流程描述
     */
    private String description;

    /**
     * 表单类型
     */
    private String formType;

    /**
     * 表单主键
     */
    private String formId;

    /**
     * 表单名称
     */
    private String formName;

    /**
     * 表单查看路由
     */
    private String formCustomViewPath;

    /**
     * 表单提交路由
     */
    private String formCustomCreatePath;

    /**
     * 流程图标
     */
    private String icon;

    /**
     * 菜单id
     */
    private String menuId;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 流程配置
     */
    private String processConfig;

    /**
     * 模板id
     */
    private String modelTemplateId;
}
