package com.ruoyi.flowable.service.impl;

import com.ruoyi.flowable.domain.WfRoamHistorical;
import com.ruoyi.flowable.mapper.WfRoamHistoricalMapper;
import com.ruoyi.flowable.service.IWfRoamHistoricalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 历史流转记录Service业务层处理
 *
 * @author fengcheng
 * @date 2024-08-16
 */
@Service
public class WfRoamHistoricalServiceImpl implements IWfRoamHistoricalService
{
    @Autowired
    private WfRoamHistoricalMapper wfRoamHistoricalMapper;

    /**
     * 查询历史流转记录
     *
     * @param roamHistoricalId 历史流转记录主键
     * @return 历史流转记录
     */
    @Override
    public WfRoamHistorical selectWfRoamHistoricalByRoamHistoricalId(String roamHistoricalId)
    {
        return wfRoamHistoricalMapper.selectWfRoamHistoricalByRoamHistoricalId(roamHistoricalId);
    }

    /**
     * 查询历史流转记录列表
     *
     * @param wfRoamHistorical 历史流转记录
     * @return 历史流转记录
     */
    @Override
    public List<WfRoamHistorical> selectWfRoamHistoricalList(WfRoamHistorical wfRoamHistorical)
    {
        return wfRoamHistoricalMapper.selectWfRoamHistoricalList(wfRoamHistorical);
    }

    /**
     * 新增历史流转记录
     *
     * @param wfRoamHistorical 历史流转记录
     * @return 结果
     */
    @Override
    public int insertWfRoamHistorical(WfRoamHistorical wfRoamHistorical)
    {
        return wfRoamHistoricalMapper.insertWfRoamHistorical(wfRoamHistorical);
    }

    /**
     * 修改历史流转记录
     *
     * @param wfRoamHistorical 历史流转记录
     * @return 结果
     */
    @Override
    public int updateWfRoamHistorical(WfRoamHistorical wfRoamHistorical)
    {
        return wfRoamHistoricalMapper.updateWfRoamHistorical(wfRoamHistorical);
    }

    /**
     * 批量删除历史流转记录
     *
     * @param roamHistoricalIds 需要删除的历史流转记录主键
     * @return 结果
     */
    @Override
    public int deleteWfRoamHistoricalByRoamHistoricalIds(String[] roamHistoricalIds)
    {
        return wfRoamHistoricalMapper.deleteWfRoamHistoricalByRoamHistoricalIds(roamHistoricalIds);
    }

    /**
     * 删除历史流转记录信息
     *
     * @param roamHistoricalId 历史流转记录主键
     * @return 结果
     */
    @Override
    public int deleteWfRoamHistoricalByRoamHistoricalId(String roamHistoricalId)
    {
        return wfRoamHistoricalMapper.deleteWfRoamHistoricalByRoamHistoricalId(roamHistoricalId);
    }
}
