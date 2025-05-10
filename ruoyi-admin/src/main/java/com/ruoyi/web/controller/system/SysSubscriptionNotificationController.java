package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * 订阅通知
 *
 * @author fengcheng
 */
@RestController
@RequestMapping("/system/subscriptionNotification")
public class SysSubscriptionNotificationController extends BaseController {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 获取用户订阅通知
     *
     * @param userName 用户名
     */
    @Anonymous
    @GetMapping("/{userName}")
    public AjaxResult getSubscriptionNotification(@PathVariable String userName) {
        return success(redisTemplate.opsForValue().get(CacheConstants.SUBSCRIPTION_NOTIFICATION + userName));
    }

    /**
     * 订阅通知
     *
     * @param userName 用户名
     * @return
     */
    @Anonymous
    @PostMapping("/{userName}")
    public AjaxResult enableNotifications(@PathVariable String userName) {
        redisTemplate.opsForValue().set(CacheConstants.SUBSCRIPTION_NOTIFICATION + userName, true);
        return success();
    }
}
