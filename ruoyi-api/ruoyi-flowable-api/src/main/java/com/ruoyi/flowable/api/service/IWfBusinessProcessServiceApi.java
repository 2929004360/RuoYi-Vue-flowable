package com.ruoyi.flowable.api.service;

import com.ruoyi.flowable.api.domain.WfBusinessProcess;

/**
 * 业务api流程Service业务层
 *
 * @author fengcheng
 * @date 2024-07-15
 */
public interface IWfBusinessProcessServiceApi {

    /**
     * 插入业务流程
     *
     * @param wfBusinessProcess
     */
    void insertWfBusinessProcess(WfBusinessProcess wfBusinessProcess);

    /**
     * 删除业务流程
     *
     * @param businessId
     * @param type
     */
    void deleteWfBusinessProcessByBusinessId(String businessId, String code);
}
