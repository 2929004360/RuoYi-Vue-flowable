package com.ruoyi.flowable.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 流程模型权限对象 wf_model_permission
 *
 * @author fengcheng
 * @date 2024-07-10
 */
public class WfModelPermission extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 流程模型权限id */
    private String modelPermissionId;

    /** 流程模型ID */
    @Excel(name = "流程模型ID")
    private String modelId;

    /** 权限id */
    @Excel(name = "权限id")
    private Long permissionId;

    /** 名称 */
    @Excel(name = "名称")
    private String name;

    /** 权限类型(1=人员权限,2=部门权限) */
    @Excel(name = "权限类型(1=人员权限,2=部门权限)")
    private String type;

    public void setModelPermissionId(String modelPermissionId)
    {
        this.modelPermissionId = modelPermissionId;
    }

    public String getModelPermissionId()
    {
        return modelPermissionId;
    }
    public void setModelId(String modelId)
    {
        this.modelId = modelId;
    }

    public String getModelId()
    {
        return modelId;
    }
    public void setPermissionId(Long permissionId)
    {
        this.permissionId = permissionId;
    }

    public Long getPermissionId()
    {
        return permissionId;
    }
    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
    public void setType(String type)
    {
        this.type = type;
    }

    public String getType()
    {
        return type;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("modelPermissionId", getModelPermissionId())
            .append("modelId", getModelId())
            .append("permissionId", getPermissionId())
            .append("name", getName())
            .append("type", getType())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
