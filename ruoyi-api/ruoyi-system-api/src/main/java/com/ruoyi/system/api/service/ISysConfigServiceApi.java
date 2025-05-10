package com.ruoyi.system.api.service;

/**
 * 参数配置Api 服务层
 *
 * @author fengcheng
 */
public interface ISysConfigServiceApi {

    /**
     * 根据键名查询参数配置信息
     *
     * @param configKey 参数键名
     * @return 参数键值
     */
    String selectConfigByKey(String configKey);
}
