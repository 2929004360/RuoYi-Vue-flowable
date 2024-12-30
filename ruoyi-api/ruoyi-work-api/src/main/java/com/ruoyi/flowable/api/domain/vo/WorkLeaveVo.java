package com.ruoyi.flowable.api.domain.vo;

import com.ruoyi.flowable.api.domain.WorkLeave;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 请假管理对象 work_leave
 *
 * @author fengcheng
 * @date 2024-12-30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkLeaveVo extends WorkLeave {
    /**
     * 业务流程类型
     */
    private String businessProcessType;

    /**
     * 业务id
     */
    private String businessId;
}
