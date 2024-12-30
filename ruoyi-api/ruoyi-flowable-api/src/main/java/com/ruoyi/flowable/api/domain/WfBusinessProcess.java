package com.ruoyi.flowable.api.domain;


import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 业务流程对象 wf_business_process
 *
 * @author fengcheng
 * @date 2024-07-15
 */
public class WfBusinessProcess extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 业务id */
    private String businessId;

    /** 流程ID */
    private String processId;

    /** 业务流程类型 */
    private String businessProcessType;

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getBusinessProcessType() {
        return businessProcessType;
    }

    public void setBusinessProcessType(String businessProcessType) {
        this.businessProcessType = businessProcessType;
    }

    @Override
    public String toString() {
        return "WfBusinessProcess{" +
                "businessId='" + businessId + '\'' +
                ", processId='" + processId + '\'' +
                ", businessProcessType='" + businessProcessType + '\'' +
                '}';
    }
}
