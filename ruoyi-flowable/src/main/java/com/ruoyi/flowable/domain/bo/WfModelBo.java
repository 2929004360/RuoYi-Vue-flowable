package com.ruoyi.flowable.domain.bo;

import com.ruoyi.flowable.base.BaseEntity;
import com.ruoyi.flowable.domain.WfModelPermission;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 流程模型对象
 *
 * @author fengcheng
 * @createTime 2022/6/21 9:16
 */
@Data
public class WfModelBo extends BaseEntity {
    /**
     * 模型主键
     */
    @NotNull(message = "模型主键不能为空")
    private String modelId;

    /**
     * 菜单id
     */
    private String menuId;

    /**
     * 模型名称
     */
    @NotNull(message = "模型名称不能为空")
    private String modelName;
    /**
     * 模型Key
     */
    @NotNull(message = "模型Key不能为空")
    private String modelKey;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 流程分类
     */
    @NotBlank(message = "流程分类不能为空")
    private String category;

    /**
     * 描述
     */
    private String description;

    /**
     * 表单类型
     */
    private String formType;

    /**
     * 流程配置
     */
    private String processConfig;

    /**
     * 模板id
     */
    private String modelTemplateId;

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
     * 流程xml
     */
    private String bpmnXml;

    /**
     * 是否保存为新版本
     */
    private Boolean newVersion;

    /**
     * 使用人
     */
    private List<WfModelPermission> selectUserList;

    /**
     * 使用部门
     */
    private List<WfModelPermission> selectDeptList;
}
