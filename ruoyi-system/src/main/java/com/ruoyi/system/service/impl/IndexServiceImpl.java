package com.ruoyi.system.service.impl;

import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.system.domain.BarChartData;
import com.ruoyi.system.domain.CardData;
import com.ruoyi.system.domain.PieChartData;
import com.ruoyi.system.mapper.IndexMapper;
import com.ruoyi.system.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 首页管理 服务层实现
 *
 * @author fengcheng
 */
@Service
public class IndexServiceImpl implements IndexService {

    @Autowired
    private IndexMapper indexMapper;

    /**
     * 获取隐患区域数据
     *
     * @param pieChartData
     * @return
     */
    @Override
    public List<PieChartData> getRiskAreaData(PieChartData pieChartData) {
        return indexMapper.getRiskAreaData(pieChartData);
    }

    /**
     * 获取近七天申报隐患数据
     *
     * @param barChartData
     * @return
     */
    @Override
    public BarChartData getRecentHazardReports(BarChartData barChartData) {
        BarChartData data = new BarChartData();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter formattedMM = DateTimeFormatter.ofPattern("MM-dd");

        List<String> nameList = new ArrayList<>();
        List<Integer> valueList = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDate data1 = today.minusDays(1);
        LocalDate data2 = today.minusDays(2);
        LocalDate data3 = today.minusDays(3);
        LocalDate data4 = today.minusDays(4);
        LocalDate data5 = today.minusDays(5);
        LocalDate data6 = today.minusDays(6);

        nameList.add(today.format(formattedMM));
        nameList.add(data1.format(formattedMM));
        nameList.add(data2.format(formattedMM));
        nameList.add(data3.format(formattedMM));
        nameList.add(data4.format(formattedMM));
        nameList.add(data5.format(formattedMM));
        nameList.add(data6.format(formattedMM));

        LocalDateTime startDate = today.atStartOfDay();
        LocalDateTime endDate = today.atStartOfDay().plusDays(1).minusNanos(1);
        valueList.add(indexMapper.countWorkRiskByDate(startDate.format(formatter), endDate.format(formatter), barChartData));

        startDate = data1.atStartOfDay();
        endDate = data1.atStartOfDay().plusDays(1).minusNanos(1);
        valueList.add(indexMapper.countWorkRiskByDate(startDate.format(formatter), endDate.format(formatter), barChartData));

        startDate = data2.atStartOfDay();
        endDate = data2.atStartOfDay().plusDays(1).minusNanos(1);
        valueList.add(indexMapper.countWorkRiskByDate(startDate.format(formatter), endDate.format(formatter), barChartData));

        startDate = data3.atStartOfDay();
        endDate = data3.atStartOfDay().plusDays(1).minusNanos(1);
        valueList.add(indexMapper.countWorkRiskByDate(startDate.format(formatter), endDate.format(formatter), barChartData));


        startDate = data4.atStartOfDay();
        endDate = data4.atStartOfDay().plusDays(1).minusNanos(1);
        valueList.add(indexMapper.countWorkRiskByDate(startDate.format(formatter), endDate.format(formatter), barChartData));

        startDate = data5.atStartOfDay();
        endDate = data5.atStartOfDay().plusDays(1).minusNanos(1);
        valueList.add(indexMapper.countWorkRiskByDate(startDate.format(formatter), endDate.format(formatter), barChartData));

        startDate = data6.atStartOfDay();
        endDate = data6.atStartOfDay().plusDays(1).minusNanos(1);
        valueList.add(indexMapper.countWorkRiskByDate(startDate.format(formatter), endDate.format(formatter), barChartData));

        data.setNameList(nameList);
        data.setValueList(valueList);
        return data;
    }

    /**
     * 获取卡片数据
     *
     * @param cardData
     * @return
     */
    @Override
    public CardData getCardData(CardData cardData) {
        CardData data = new CardData();
        data.setUserCount(indexMapper.selectCountUser(cardData));
        data.setRiskCount(indexMapper.selectCountRisk(cardData));
        data.setCompleteRiskCount(indexMapper.selectCountCompleteRisk(cardData));
        data.setTodayRiskCount(indexMapper.selectCountTodayRisk(cardData));
        return data;
    }
}
