package com.ruoyi.common.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 流程启动数据
 *
 * @author fengcheng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessStartData implements Serializable {

    /**
     * 业务流程类型
     */
    private String businessProcessType;

    /**
     * 业务id
     */
    private String businessId;

    /**
     * 数据
     */
    private Object data;
}
