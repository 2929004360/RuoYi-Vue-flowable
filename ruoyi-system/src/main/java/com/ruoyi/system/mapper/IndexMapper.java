package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.BarChartData;
import com.ruoyi.system.domain.CardData;
import com.ruoyi.system.domain.PieChartData;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 首页管理 数据层
 *
 * @author fengcheng
 */
public interface IndexMapper {

    /**
     * 获取隐患区域数据
     *
     * @param pieChartData
     * @return
     */
    List<PieChartData> getRiskAreaData(PieChartData pieChartData);

    /**
     * 统计隐患数量
     *
     * @param startDate
     * @param endDate
     * @param barChartData
     * @return
     */
    int countWorkRiskByDate(@Param("startDate") String startDate, @Param("endDate") String endDate,@Param("barChartData") BarChartData barChartData);

    /**
     * 统计用户数量
     *
     * @param cardData
     * @return
     */
    Integer selectCountUser(CardData cardData);

    /**
     * 统计隐患数量
     *
     * @param cardData
     * @return
     */
    Integer selectCountRisk(CardData cardData);

    /**
     * 统计已完成隐患数量
     *
     * @param cardData
     * @return
     */
    Integer selectCountCompleteRisk(CardData cardData);

    /**
     * 统计今日隐患数量
     *
     * @param cardData
     * @return
     */
    Integer selectCountTodayRisk(CardData cardData);
}
