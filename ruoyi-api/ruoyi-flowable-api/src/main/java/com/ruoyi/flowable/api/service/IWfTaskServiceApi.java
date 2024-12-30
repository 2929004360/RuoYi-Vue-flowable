package com.ruoyi.flowable.api.service;

import com.ruoyi.flowable.api.domain.bo.WfTaskBo;

/**
 * 流程任务api
 *
 * @author fengcheng
 * @date 2024-07-15
 */
public interface IWfTaskServiceApi {

    /**
     * 取消申请
     *
     * @param wfTaskBo
     */
    void stopProcess(WfTaskBo wfTaskBo);
}
