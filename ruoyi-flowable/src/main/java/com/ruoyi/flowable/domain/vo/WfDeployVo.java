package com.ruoyi.flowable.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import lombok.Data;

import java.util.Date;


/**
 * 流程部署视图对象
 *
 * @author fengcheng
 * @date 2022-06-30
 */
@Data
public class WfDeployVo {

    private static final long serialVersionUID = 1L;

    /**
     * 流程定义ID
     */
    @Excel(name = "流程定义ID")
    private String definitionId;

    /**
     * 流程名称
     */
    @Excel(name = "流程名称")
    private String processName;

    /**
     * 流程Key
     */
    @Excel(name = "流程Key")
    private String processKey;

    /**
     * 分类编码
     */
    @Excel(name = "分类编码")
    private String category;

    /**
     * 版本
     */
    private Integer version;

    /**
     * 表单ID
     */
    @Excel(name = "表单ID")
    private Long formId;

    /**
     * 表单名称
     */
    @Excel(name = "表单名称")
    private String formName;

    /**
     * 部署ID
     */
    @Excel(name = "部署ID")
    private String deploymentId;

    /**
     * 流程定义状态: 1:激活 , 2:中止
     */
    @Excel(name = "流程定义状态",readConverterExp = "1=激活,2=中止")
    private Boolean suspended;

    /**
     * 部署时间
     */
    @Excel(name = "部署时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deploymentTime;

    /**
     * 流程图标
     */
    private String icon;
}
