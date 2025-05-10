package com.ruoyi.flowable.api.domain.vo;

import com.ruoyi.flowable.api.domain.WorkRisk;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 隐患上报对象 work_risk
 *
 * @author fengcheng
 * @date 2025-03-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkRiskVo extends WorkRisk {

    /**
     * 业务流程类型
     */
    private String businessProcessType;

    /**
     * 业务id
     */
    private String businessId;
}
