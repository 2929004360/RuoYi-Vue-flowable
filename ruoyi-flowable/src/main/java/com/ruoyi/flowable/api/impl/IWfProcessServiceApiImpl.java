package com.ruoyi.flowable.api.impl;

import com.ruoyi.flowable.api.service.IWfProcessServiceApi;
import com.ruoyi.flowable.service.IWfProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 流程服务api接口处理
 *
 * @author fengcheng
 * @createTime 2022/3/24 18:57
 */
@RequiredArgsConstructor
@Service
public class IWfProcessServiceApiImpl implements IWfProcessServiceApi {

    private final IWfProcessService processService;

    /**
     * 启动流程
     *
     * @param definitionId
     * @param beanToMap
     * @return
     */
    @Override
    public String startProcessByDefId(String definitionId, Map<String, Object> beanToMap) {
        return processService.startProcessByDefId(definitionId, beanToMap).getProcessInstanceId();
    }

    /**
     * 删除流程实例
     *
     * @param instanceIds
     */
    @Override
    public void deleteProcessByIds(String[] instanceIds) {
        processService.deleteProcessByIds(instanceIds);
    }
}
