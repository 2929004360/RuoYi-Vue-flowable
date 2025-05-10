package com.ruoyi.system.service;

import com.ruoyi.system.domain.BarChartData;
import com.ruoyi.system.domain.CardData;
import com.ruoyi.system.domain.PieChartData;

import java.util.List;

/**
 * 首页管理 服务层
 *
 * @author fengcheng
 */
public interface IndexService {

    /**
     * 获取隐患区域数据
     *
     * @param pieChartData
     * @return
     */
    List<PieChartData> getRiskAreaData(PieChartData pieChartData);

    /**
     * 获取近七天申报隐患数据
     *
     * @param barChartData
     * @return
     */
    BarChartData getRecentHazardReports(BarChartData barChartData);

    /**
     * 获取卡片数据
     *
     * @param cardData
     * @return
     */
    CardData getCardData(CardData cardData);
}
