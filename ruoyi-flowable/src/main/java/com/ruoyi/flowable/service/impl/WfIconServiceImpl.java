package com.ruoyi.flowable.service.impl;

import com.ruoyi.flowable.domain.WfIcon;
import com.ruoyi.flowable.mapper.WfIconMapper;
import com.ruoyi.flowable.service.IWfIconService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 流程图标Service业务层处理
 *
 * @author fengcheng
 * @date 2024-07-09
 */
@Service
public class WfIconServiceImpl implements IWfIconService {
    @Autowired
    private WfIconMapper wfIconMapper;

    /**
     * 查询流程图标
     *
     * @param deploymentId 流程图标主键
     * @return 流程图标
     */
    @Override
    public WfIcon selectWfIconByDeploymentId(String deploymentId) {
        return wfIconMapper.selectWfIconByDeploymentId(deploymentId);
    }

    /**
     * 查询流程图标列表
     *
     * @param wfIcon 流程图标
     * @return 流程图标
     */
    @Override
    public List<WfIcon> selectWfIconList(WfIcon wfIcon) {
        return wfIconMapper.selectWfIconList(wfIcon);
    }

    /**
     * 新增流程图标
     *
     * @param wfIcon 流程图标
     * @return 结果
     */
    @Override
    public int insertWfIcon(WfIcon wfIcon) {
        return wfIconMapper.insertWfIcon(wfIcon);
    }

    /**
     * 修改流程图标
     *
     * @param wfIcon 流程图标
     * @return 结果
     */
    @Override
    public int updateWfIcon(WfIcon wfIcon) {
        return wfIconMapper.updateWfIcon(wfIcon);
    }

    /**
     * 批量删除流程图标
     *
     * @param deploymentIds 需要删除的流程图标主键
     * @return 结果
     */
    @Override
    public int deleteWfIconByDeploymentIds(String[] deploymentIds) {
        return wfIconMapper.deleteWfIconByDeploymentIds(deploymentIds);
    }

    /**
     * 删除流程图标信息
     *
     * @param deploymentId 流程图标主键
     * @return 结果
     */
    @Override
    public int deleteWfIconByDeploymentId(String deploymentId) {
        return wfIconMapper.deleteWfIconByDeploymentId(deploymentId);
    }
}
