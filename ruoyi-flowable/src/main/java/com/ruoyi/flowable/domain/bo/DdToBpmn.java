package com.ruoyi.flowable.domain.bo;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * @author fengcheng
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DdToBpmn extends BaseEntity {

    /**
     * json数据
     */
    @NotNull(message = "json数据不能为空")
    private String json;

    /**
     * 流程名称
     */
    @NotNull(message = "流程名称不能为空")
    private String name;
}
