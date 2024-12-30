package com.ruoyi.flowable.domain.bo;

import com.ruoyi.flowable.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 流程表单业务对象
 *
 * @author fengcheng
 * @createTime 2022/3/7 22:07
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WfFormBo extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 表单主键
     */
    @NotNull(message = "表单ID不能为空")
    private String formId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 部门id
     */
    @NotNull(message = "部门id不能为空")
    private Long deptId;

    /**
     * 创建人
     */
    private String userName;

    /**
     * 部门名称
     */
    @NotNull(message = "部门名称不能为空")
    private String deptName;

    /**
     * 表单名称
     */
    @NotBlank(message = "表单名称不能为空")
    private String formName;

    /**
     * 使用类型(1=本级使用,2=本下级使用)
     */
    @NotBlank(message = "使用类型不能为空")
    private String type;

    /**
     * 表单内容
     */
    @NotBlank(message = "表单内容不能为空")
    private String content;

    /**
     * 备注
     */
    private String remark;
}
