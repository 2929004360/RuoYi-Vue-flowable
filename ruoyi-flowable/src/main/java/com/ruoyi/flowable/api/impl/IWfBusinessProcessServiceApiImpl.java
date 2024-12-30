package com.ruoyi.flowable.api.impl;

import com.ruoyi.flowable.api.domain.WfBusinessProcess;
import com.ruoyi.flowable.api.service.IWfBusinessProcessServiceApi;
import com.ruoyi.flowable.service.IWfBusinessProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 业务api流程Service业务层处理
 *
 * @author fengcheng
 * @date 2024-07-15
 */
@RequiredArgsConstructor
@Service
public class IWfBusinessProcessServiceApiImpl implements IWfBusinessProcessServiceApi {

    private final IWfBusinessProcessService wfBusinessProcessService;

    /**
     * 插入业务流程
     *
     * @param wfBusinessProcess
     */
    @Override
    public void insertWfBusinessProcess(WfBusinessProcess wfBusinessProcess) {
        wfBusinessProcessService.insertWfBusinessProcess(wfBusinessProcess);
    }

    /**
     * 删除业务流程
     *
     * @param businessId
     * @param type
     */
    @Override
    public void deleteWfBusinessProcessByBusinessId(String businessId, String type) {
        wfBusinessProcessService.deleteWfBusinessProcessByBusinessId(businessId, type);
    }
}
