package com.ruoyi.system.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 卡数据对象
 *
 * @author fengcheng
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CardData extends BaseEntity {

    /**
     * 用户总数
     */
    private Integer userCount;

    /**
     * 隐患总数
     */
    private Integer riskCount;

    /**
     * 完成隐患总数
     */
    private Integer completeRiskCount;

    /**
     * 今日隐患总数
     */
    private Integer todayRiskCount;
}
