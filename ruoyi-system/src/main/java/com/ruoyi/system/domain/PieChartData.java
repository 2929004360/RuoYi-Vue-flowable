package com.ruoyi.system.domain;


import com.ruoyi.common.core.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 饼图数据
 *
 * @author fengcheng
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PieChartData extends BaseEntity {

    private String name;

    private Integer value;
}
