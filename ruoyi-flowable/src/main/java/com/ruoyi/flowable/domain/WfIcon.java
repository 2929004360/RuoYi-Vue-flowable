package com.ruoyi.flowable.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 流程图标对象 wf_icon
 *
 * @author fengcheng
 * @date 2024-07-09
 */
public class WfIcon extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 流程部署id */
    @Excel(name = "流程部署id")
    private String deploymentId;

    /** 流程图标路径 */
    @Excel(name = "流程图标路径")
    private String icon;

    public void setDeploymentId(String deploymentId)
    {
        this.deploymentId = deploymentId;
    }

    public String getDeploymentId()
    {
        return deploymentId;
    }
    public void setIcon(String icon)
    {
        this.icon = icon;
    }

    public String getIcon()
    {
        return icon;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("deploymentId", getDeploymentId())
            .append("icon", getIcon())
            .toString();
    }
}
