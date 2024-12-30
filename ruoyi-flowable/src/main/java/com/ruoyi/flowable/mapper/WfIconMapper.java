package com.ruoyi.flowable.mapper;

import com.ruoyi.flowable.domain.WfIcon;

import java.util.List;

/**
 * 流程图标Mapper接口
 *
 * @author fengcheng
 * @date 2024-07-09
 */
public interface WfIconMapper
{
    /**
     * 查询流程图标
     *
     * @param deploymentId 流程图标主键
     * @return 流程图标
     */
    public WfIcon selectWfIconByDeploymentId(String deploymentId);

    /**
     * 查询流程图标列表
     *
     * @param wfIcon 流程图标
     * @return 流程图标集合
     */
    public List<WfIcon> selectWfIconList(WfIcon wfIcon);

    /**
     * 新增流程图标
     *
     * @param wfIcon 流程图标
     * @return 结果
     */
    public int insertWfIcon(WfIcon wfIcon);

    /**
     * 修改流程图标
     *
     * @param wfIcon 流程图标
     * @return 结果
     */
    public int updateWfIcon(WfIcon wfIcon);

    /**
     * 删除流程图标
     *
     * @param deploymentId 流程图标主键
     * @return 结果
     */
    public int deleteWfIconByDeploymentId(String deploymentId);

    /**
     * 批量删除流程图标
     *
     * @param deploymentIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteWfIconByDeploymentIds(String[] deploymentIds);
}
