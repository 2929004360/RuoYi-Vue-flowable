package com.ruoyi.system.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 柱状图数据
 *
 * @author fengcheng
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BarChartData extends BaseEntity {

    private List<String> nameList;

    private List<Integer> valueList;
}
