package com.ruoyi.flowable.domain.vo;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.flowable.base.BaseEntity;
import lombok.Data;

/**
 * 流程分类视图对象
 *
 * @author fengcheng
 * @createTime 2022/3/7 22:07
 */
@Data
public class WfFormVo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 表单主键
     */
    @Excel(name = "表单ID")
    private String formId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 部门id
     */
    private Long deptId;

    /**
     * 创建人
     */
    @Excel(name = "创建人")
    private String userName;

    /**
     * 部门名称
     */
    @Excel(name = "部门名称")
    private String deptName;

    /**
     * 使用类型(1=本级使用,2=本下级使用)
     */
    @Excel(name = "使用类型", readConverterExp = "1=本级使用,2=本下级使用")
    private String type;

    /**
     * 表单名称
     */
    @Excel(name = "表单名称")
    private String formName;

    /**
     * 表单内容
     */
    @Excel(name = "表单内容")
    private String content;

    /**
     * 备注
     */
    @Excel(name = "备注")
    private String remark;
}
