package com.ruoyi.flowable.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 模型模板对象 wf_model_template
 *
 * @author fengcheng
 * @date 2024-07-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wf_model_template")
public class WfModelTemplate extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 模型模板id
     */
    private String modelTemplateId;

    /**
     * 部门id
     */
    private Long deptId;

    /**
     * 模板名称
     */
    @Excel(name = "模板名称" )
    private String name;

    /**
     * 部门名称
     */
    @Excel(name = "部门名称" )
    private String deptName;

    /**
     * 使用类型(1=本级使用,2=本下级使用)
     */
    @Excel(name = "使用类型" , readConverterExp = "1=本级使用,2=本下级使用" )
    private String type;

    /**
     * 流程xml
     */
    @Excel(name = "流程xml")
    private String bpmnXml;

    /**
     * 表单类型
     */
    @Excel(name = "表单类型" , readConverterExp = "0=流程表单.1=外置表单,2=节点独立表单" )
    private String formType;
}
