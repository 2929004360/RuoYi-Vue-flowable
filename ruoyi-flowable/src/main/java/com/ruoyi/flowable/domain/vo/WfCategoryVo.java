package com.ruoyi.flowable.domain.vo;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.flowable.base.BaseEntity;
import lombok.Data;


/**
 * 流程分类视图对象 flow_category
 *
 * @author fengcheng
 * @date 2022-01-15
 */
@Data
public class WfCategoryVo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 分类ID
     */
    private String categoryId;

    /**
     * 分类名称
     */
    @Excel(name = "分类名称")
    private String categoryName;

    /**
     * 分类编码
     */
    @Excel(name = "分类编码")
    private String code;

    /**
     * 备注
     */
    @Excel(name = "备注")
    private String remark;
}
