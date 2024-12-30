package com.ruoyi.flowable.api.service;

import java.util.Map;

/**
 * 流程服务api接口
 *
 * @author fengcheng
 * @createTime 2022/3/24 18:57
 */
public interface IWfProcessServiceApi {

    /**
     * 启动流程
     *
     * @param definitionId
     * @param beanToMap
     * @return
     */
    String startProcessByDefId(String definitionId, Map<String, Object> beanToMap);

    /**
     * 删除流程实例
     *
     * @param instanceIds
     */
    void deleteProcessByIds(String[] strings);
}
