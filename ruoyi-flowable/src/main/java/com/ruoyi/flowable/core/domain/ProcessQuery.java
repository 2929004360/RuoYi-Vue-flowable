package com.ruoyi.flowable.core.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 流程查询实体对象
 *
 * @author fengcheng
 * @createTime 2022/6/11 01:15
 */
@Data
public class ProcessQuery extends BaseEntity {

    /**
     * 流程标识
     */
    private String processKey;

    /**
     * 流程名称
     */
    private String processName;

    /**
     * 流程分类
     */
    private String category;

    /**
     * 状态
     */
    private String state;

    /**
     * 完成状态(false=未完成,true=完成)
     */
    private Boolean isComplete;

    /**
     * 请求参数
     */
    private Map<String, Object> params = new HashMap<>();
}
