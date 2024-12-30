package com.ruoyi.flowable.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.flowable.domain.WfModelPermission;
import com.ruoyi.flowable.mapper.WfModelPermissionMapper;
import com.ruoyi.flowable.service.IWfModelPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 流程模型权限Service业务层处理
 *
 * @author fengcheng
 * @date 2024-07-10
 */
@Service
public class WfModelPermissionServiceImpl implements IWfModelPermissionService {
    @Autowired
    private WfModelPermissionMapper wfModelPermissionMapper;

    /**
     * 查询流程模型权限
     *
     * @param modelPermissionId 流程模型权限主键
     * @return 流程模型权限
     */
    @Override
    public WfModelPermission selectWfModelPermissionByModelPermissionId(String modelPermissionId) {
        return wfModelPermissionMapper.selectWfModelPermissionByModelPermissionId(modelPermissionId);
    }

    /**
     * 查询流程模型权限列表
     *
     * @param wfModelPermission 流程模型权限
     * @param permissionIdList  业务id列表
     * @return 流程模型权限
     */
    @Override
    public List<WfModelPermission> selectWfModelPermissionList(WfModelPermission wfModelPermission, List<Long> permissionIdList) {
        return wfModelPermissionMapper.selectWfModelPermissionList(wfModelPermission, permissionIdList);
    }

    /**
     * 新增流程模型权限
     *
     * @param wfModelPermission 流程模型权限
     * @return 结果
     */
    @Override
    public int insertWfModelPermission(WfModelPermission wfModelPermission) {
        wfModelPermission.setCreateTime(DateUtils.getNowDate());
        return wfModelPermissionMapper.insertWfModelPermission(wfModelPermission);
    }

    /**
     * 修改流程模型权限
     *
     * @param wfModelPermission 流程模型权限
     * @return 结果
     */
    @Override
    public int updateWfModelPermission(WfModelPermission wfModelPermission) {
        wfModelPermission.setUpdateTime(DateUtils.getNowDate());
        return wfModelPermissionMapper.updateWfModelPermission(wfModelPermission);
    }

    /**
     * 批量删除流程模型权限
     *
     * @param modelPermissionIds 需要删除的流程模型权限主键
     * @return 结果
     */
    @Override
    public int deleteWfModelPermissionByModelPermissionIds(String[] modelPermissionIds) {
        return wfModelPermissionMapper.deleteWfModelPermissionByModelPermissionIds(modelPermissionIds);
    }

    /**
     * 删除流程模型权限信息
     *
     * @param modelPermissionId 流程模型权限主键
     * @return 结果
     */
    @Override
    public int deleteWfModelPermissionByModelPermissionId(String modelPermissionId) {
        return wfModelPermissionMapper.deleteWfModelPermissionByModelPermissionId(modelPermissionId);
    }

    /**
     * 批量新增流程模型权限
     *
     * @param permissionsList
     * @return
     */
    @Override
    public int insertWfModelPermissionList(List<WfModelPermission> permissionsList) {
        return wfModelPermissionMapper.insertWfModelPermissionList(permissionsList);
    }

    /**
     * 根据模型ID删除流程模型权限
     *
     * @param modelId
     * @return
     */
    @Override
    public int deleteWfModelPermissionByModelId(String modelId) {
        return wfModelPermissionMapper.deleteWfModelPermissionByModelId(modelId);
    }
}
