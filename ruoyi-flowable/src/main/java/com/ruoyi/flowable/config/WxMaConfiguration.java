package com.ruoyi.flowable.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.bean.WxMaKefuMessage;
import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import cn.binarywang.wx.miniapp.message.WxMaMessageHandler;
import cn.binarywang.wx.miniapp.message.WxMaMessageRouter;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.error.WxRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 微信小程序配置
 *
 * @author fengcheng
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(WxMaProperties.class)
public class WxMaConfiguration {
    private final WxMaProperties properties;

    @Autowired
    public WxMaConfiguration(WxMaProperties properties) {
        this.properties = properties;
    }

    @Bean
    public WxMaService wxMaService() {
        List<WxMaProperties.Config> configs = this.properties.getConfigs();
        if (configs == null) {
            throw new WxRuntimeException("大哥，拜托先看下项目首页的说明（readme文件），添加下相关配置，注意别配错了！");
        }
        WxMaService maService = new WxMaServiceImpl();
        maService.setMultiConfigs(
            configs.stream()
                .map(a -> {
                    WxMaDefaultConfigImpl config = new WxMaDefaultConfigImpl();
//                WxMaDefaultConfigImpl config = new WxMaRedisConfigImpl(new JedisPool());
                    // 使用上面的配置时，需要同时引入jedis-lock的依赖，否则会报类无法找到的异常
                    config.setAppid(a.getAppid());
                    config.setSecret(a.getSecret());
                    config.setToken(a.getToken());
                    config.setAesKey(a.getAesKey());
                    config.setMsgDataFormat(a.getMsgDataFormat());
                    return config;
                }).collect(Collectors.toMap(WxMaDefaultConfigImpl::getAppid, a -> a, (o, n) -> o)));
        return maService;
    }
}
