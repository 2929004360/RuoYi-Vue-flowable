package com.ruoyi.flowable.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 模型关联模板对象 wf_model_association_template
 *
 * @author fengcheng
 * @date 2024-07-23
 */
public class WfModelAssociationTemplate extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 模型模板id */
    @Excel(name = "模型模板id")
    private String modelTemplateId;

    /** 模型ID */
    @Excel(name = "模型ID")
    private String modelId;

    public void setModelTemplateId(String modelTemplateId)
    {
        this.modelTemplateId = modelTemplateId;
    }

    public String getModelTemplateId()
    {
        return modelTemplateId;
    }
    public void setModelId(String modelId)
    {
        this.modelId = modelId;
    }

    public String getModelId()
    {
        return modelId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("modelTemplateId", getModelTemplateId())
            .append("modelId", getModelId())
            .toString();
    }
}
