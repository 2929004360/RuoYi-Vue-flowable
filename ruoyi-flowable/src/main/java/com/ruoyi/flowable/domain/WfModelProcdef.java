package com.ruoyi.flowable.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 模型部署对象 wf_model_procdef
 *
 * @author fengcheng
 * @date 2024-07-11
 */
public class WfModelProcdef extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 模型ID */
    private String modelId;

    /** 部署id */
    private String procdefId;

    /**
     * 表单类型
     */
    private String formType;

    /**
     * 表单创建路径
     */
    private String formCreatePath;

    /**
     * 表单查看路由
     */
    private String formViewPath;

    public void setModelId(String modelId)
    {
        this.modelId = modelId;
    }

    public String getModelId()
    {
        return modelId;
    }
    public void setProcdefId(String procdefId)
    {
        this.procdefId = procdefId;
    }

    public String getProcdefId()
    {
        return procdefId;
    }

    public String getFormType() {
        return formType;
    }

    public void setFormType(String formType) {
        this.formType = formType;
    }

    public String getFormCreatePath() {
        return formCreatePath;
    }

    public void setFormCreatePath(String formCreatePath) {
        this.formCreatePath = formCreatePath;
    }

    public String getFormViewPath() {
        return formViewPath;
    }

    public void setFormViewPath(String formViewPath) {
        this.formViewPath = formViewPath;
    }

    @Override
    public String toString() {
        return "WfModelProcdef{" +
                "modelId='" + modelId + '\'' +
                ", procdefId='" + procdefId + '\'' +
                ", formType='" + formType + '\'' +
                ", formCreatePath='" + formCreatePath + '\'' +
                ", formViewPath='" + formViewPath + '\'' +
                '}';
    }
}

