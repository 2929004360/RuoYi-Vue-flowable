package com.ruoyi.flowable.service;

import com.ruoyi.flowable.domain.WfModelProcdef;

import java.util.List;

/**
 * 模型部署Service接口
 *
 * @author fengcheng
 * @date 2024-07-11
 */
public interface IWfModelProcdefService
{
    /**
     * 查询模型部署
     *
     * @param modelId 模型部署主键
     * @return 模型部署
     */
    public WfModelProcdef selectWfModelProcdefByModelId(String modelId);

    /**
     * 查询模型部署列表
     *
     * @param wfModelProcdef 模型部署
     * @return 模型部署集合
     */
    public List<WfModelProcdef> selectWfModelProcdefList(WfModelProcdef wfModelProcdef);

    /**
     * 新增模型部署
     *
     * @param wfModelProcdef 模型部署
     * @return 结果
     */
    public int insertWfModelProcdef(WfModelProcdef wfModelProcdef);

    /**
     * 修改模型部署
     *
     * @param wfModelProcdef 模型部署
     * @return 结果
     */
    public int updateWfModelProcdef(WfModelProcdef wfModelProcdef);

    /**
     * 批量删除模型部署
     *
     * @param modelIds 需要删除的模型部署主键集合
     * @return 结果
     */
    public int deleteWfModelProcdefByModelIds(String[] modelIds);

    /**
     * 删除模型部署信息
     *
     * @param modelId 模型部署主键
     * @return 结果
     */
    public int deleteWfModelProcdefByModelId(String modelId);

    /**
     * 根据模型id列表查询模型部署id
     *
     * @param modelIdList
     * @return
     */
    List<String> selectWfModelProcdefListByModelIdList(List<String> modelIdList);

    /**
     * 根据部署id删除模型部署信息
     *
     * @param deployId
     * @return
     */
    int deleteWfModelProcdefByProcdefId(String deployId);

    /**
     * 根据部署id查询模型部署信息
     *
     * @param procdefId
     * @return
     */
    WfModelProcdef selectWfModelProcdefByProcdefId(String procdefId);
}
