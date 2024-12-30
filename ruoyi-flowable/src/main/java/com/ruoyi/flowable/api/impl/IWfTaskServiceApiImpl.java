package com.ruoyi.flowable.api.impl;


import com.ruoyi.flowable.api.domain.bo.WfTaskBo;
import com.ruoyi.flowable.api.service.IWfTaskServiceApi;
import com.ruoyi.flowable.service.IWfTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 流程任务api业务层处理
 *
 * @author fengcheng
 * @date 2024-07-15
 */
@RequiredArgsConstructor
@Service
public class IWfTaskServiceApiImpl implements IWfTaskServiceApi {

    private final IWfTaskService wfTaskService;

    /**
     * 取消申请
     *
     * @param wfTaskBo
     */
    @Override
    public void stopProcess(WfTaskBo wfTaskBo) {
        wfTaskService.stopProcess(wfTaskBo);
    }
}
