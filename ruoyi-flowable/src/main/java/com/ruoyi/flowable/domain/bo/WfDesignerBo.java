package com.ruoyi.flowable.domain.bo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 流程设计业务对象
 *
 * @author fengcheng
 * @createTime 2022/3/10 00:12
 */
@Data
public class WfDesignerBo {

    /**
     * 流程名称
     */
    @NotNull(message = "流程名称")
    private String name;

    /**
     * 流程分类
     */
    @NotBlank(message = "流程分类")
    private String category;

    /**
     * XML字符串
     */
    @NotBlank(message = "XML字符串")
    private String xml;
}
