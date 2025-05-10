package com.ruoyi.web.controller.system;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.BarChartData;
import com.ruoyi.system.domain.CardData;
import com.ruoyi.system.domain.PieChartData;
import com.ruoyi.system.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 首页管理Controller
 *
 * @author fengcheng
 * @date 2025-3-5
 */
@Validated
@RestController
@RequestMapping("/index")
public class IndexController extends BaseController {

    @Autowired
    private IndexService indexService;


    /**
     * 获取隐患区域数据
     */
    @GetMapping("/getRiskAreaData")
    public AjaxResult getRiskAreaData() {
        return success(indexService.getRiskAreaData(new PieChartData()));
    }

    /**
     * 获取近七天申报隐患数据
     */
    @GetMapping("/getRecentHazardReports")
    public AjaxResult getRecentHazardReports() {
        return success(indexService.getRecentHazardReports(new BarChartData()));
    }

    /**
     * 获取卡片数据
     */
    @GetMapping("/getCardData")
    public AjaxResult getCardData() {
        return success(indexService.getCardData(new CardData()));
    }
}
