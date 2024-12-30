package com.ruoyi.flowable.mapper;

import com.ruoyi.flowable.domain.WfRoamHistorical;

import java.util.List;

/**
 * 历史流转记录Mapper接口
 *
 * @author fengcheng
 * @date 2024-08-16
 */
public interface WfRoamHistoricalMapper
{
    /**
     * 查询历史流转记录
     *
     * @param roamHistoricalId 历史流转记录主键
     * @return 历史流转记录
     */
    public WfRoamHistorical selectWfRoamHistoricalByRoamHistoricalId(String roamHistoricalId);

    /**
     * 查询历史流转记录列表
     *
     * @param wfRoamHistorical 历史流转记录
     * @return 历史流转记录集合
     */
    public List<WfRoamHistorical> selectWfRoamHistoricalList(WfRoamHistorical wfRoamHistorical);

    /**
     * 新增历史流转记录
     *
     * @param wfRoamHistorical 历史流转记录
     * @return 结果
     */
    public int insertWfRoamHistorical(WfRoamHistorical wfRoamHistorical);

    /**
     * 修改历史流转记录
     *
     * @param wfRoamHistorical 历史流转记录
     * @return 结果
     */
    public int updateWfRoamHistorical(WfRoamHistorical wfRoamHistorical);

    /**
     * 删除历史流转记录
     *
     * @param roamHistoricalId 历史流转记录主键
     * @return 结果
     */
    public int deleteWfRoamHistoricalByRoamHistoricalId(String roamHistoricalId);

    /**
     * 批量删除历史流转记录
     *
     * @param roamHistoricalIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteWfRoamHistoricalByRoamHistoricalIds(String[] roamHistoricalIds);
}
