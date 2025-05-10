package com.ruoyi.system.api.impl;

import com.ruoyi.system.api.service.ISysConfigServiceApi;
import com.ruoyi.system.service.ISysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 参数配置API 服务实现
 *
 * @author fengcheng
 */
@Service
public class SysConfigServiceApiImpl implements ISysConfigServiceApi {

    @Autowired
    private ISysConfigService sysConfigService;

    /**
     * 根据键名查询参数配置信息
     *
     * @param configKey 参数键名
     * @return 参数键值
     */
    @Override
    public String selectConfigByKey(String configKey) {
        return sysConfigService.selectConfigByKey(configKey);
    }
}
