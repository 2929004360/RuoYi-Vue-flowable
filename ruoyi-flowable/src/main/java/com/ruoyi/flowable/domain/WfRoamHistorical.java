package com.ruoyi.flowable.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 历史流转记录对象 wf_roam_historical
 *
 * @author fengcheng
 * @date 2024-08-16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WfRoamHistorical extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 历史流转记录ID
     */
    private String roamHistoricalId;

    /**
     * 业务id
     */
    private String businessId;

    /**
     * 流程ID
     */
    private String processId;

    /**
     * 业务流程类型
     */
    private String businessProcessType;

    /**
     * 历史流转记录数据
     */
    private String data;
}
