package com.ruoyi.flowable.service.impl;

import com.ruoyi.flowable.domain.WfModelProcdef;
import com.ruoyi.flowable.mapper.WfModelProcdefMapper;
import com.ruoyi.flowable.service.IWfModelProcdefService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 模型部署Service业务层处理
 *
 * @author fengcheng
 * @date 2024-07-11
 */
@Service
public class WfModelProcdefServiceImpl implements IWfModelProcdefService
{
    @Autowired
    private WfModelProcdefMapper wfModelProcdefMapper;

    /**
     * 查询模型部署
     *
     * @param modelId 模型部署主键
     * @return 模型部署
     */
    @Override
    public WfModelProcdef selectWfModelProcdefByModelId(String modelId)
    {
        return wfModelProcdefMapper.selectWfModelProcdefByModelId(modelId);
    }

    /**
     * 查询模型部署列表
     *
     * @param wfModelProcdef 模型部署
     * @return 模型部署
     */
    @Override
    public List<WfModelProcdef> selectWfModelProcdefList(WfModelProcdef wfModelProcdef)
    {
        return wfModelProcdefMapper.selectWfModelProcdefList(wfModelProcdef);
    }

    /**
     * 新增模型部署
     *
     * @param wfModelProcdef 模型部署
     * @return 结果
     */
    @Override
    public int insertWfModelProcdef(WfModelProcdef wfModelProcdef)
    {
        return wfModelProcdefMapper.insertWfModelProcdef(wfModelProcdef);
    }

    /**
     * 修改模型部署
     *
     * @param wfModelProcdef 模型部署
     * @return 结果
     */
    @Override
    public int updateWfModelProcdef(WfModelProcdef wfModelProcdef)
    {
        return wfModelProcdefMapper.updateWfModelProcdef(wfModelProcdef);
    }

    /**
     * 批量删除模型部署
     *
     * @param modelIds 需要删除的模型部署主键
     * @return 结果
     */
    @Override
    public int deleteWfModelProcdefByModelIds(String[] modelIds)
    {
        return wfModelProcdefMapper.deleteWfModelProcdefByModelIds(modelIds);
    }

    /**
     * 删除模型部署信息
     *
     * @param modelId 模型部署主键
     * @return 结果
     */
    @Override
    public int deleteWfModelProcdefByModelId(String modelId)
    {
        return wfModelProcdefMapper.deleteWfModelProcdefByModelId(modelId);
    }

    /**
     * 根据模型id列表查询流程定义id
     *
     * @param modelIdList
     * @return
     */
    @Override
    public List<String> selectWfModelProcdefListByModelIdList(List<String> modelIdList) {
        return wfModelProcdefMapper.selectWfModelProcdefListByModelIdList(modelIdList);
    }

    /**
     * 根据部署id删除模型部署信息
     *
     * @param deployId
     * @return
     */
    @Override
    public int deleteWfModelProcdefByProcdefId(String deployId) {
        return wfModelProcdefMapper.deleteWfModelProcdefByProcdefId(deployId);
    }

    /**
     * 根据部署id查询模型部署信息
     *
     * @param procdefId
     * @return
     */
    @Override
    public WfModelProcdef selectWfModelProcdefByProcdefId(String procdefId) {
        return wfModelProcdefMapper.selectWfModelProcdefByProcdefId(procdefId);
    }
}
