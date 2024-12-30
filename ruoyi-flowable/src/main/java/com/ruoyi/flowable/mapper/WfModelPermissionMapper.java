package com.ruoyi.flowable.mapper;

import com.ruoyi.flowable.domain.WfModelPermission;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 流程模型权限Mapper接口
 *
 * @author fengcheng
 * @date 2024-07-10
 */
public interface WfModelPermissionMapper {
    /**
     * 查询流程模型权限
     *
     * @param modelPermissionId 流程模型权限主键
     * @return 流程模型权限
     */
    public WfModelPermission selectWfModelPermissionByModelPermissionId(String modelPermissionId);

    /**
     * 查询流程模型权限列表
     *
     * @param wfModelPermission 流程模型权限
     * @param permissionIdList  业务id列表
     * @return 流程模型权限集合
     */
    public List<WfModelPermission> selectWfModelPermissionList(
            @Param("wfModelPermission") WfModelPermission wfModelPermission,
            @Param("permissionIdList") List<Long> permissionIdList
    );

    /**
     * 新增流程模型权限
     *
     * @param wfModelPermission 流程模型权限
     * @return 结果
     */
    public int insertWfModelPermission(WfModelPermission wfModelPermission);

    /**
     * 修改流程模型权限
     *
     * @param wfModelPermission 流程模型权限
     * @return 结果
     */
    public int updateWfModelPermission(WfModelPermission wfModelPermission);

    /**
     * 删除流程模型权限
     *
     * @param modelPermissionId 流程模型权限主键
     * @return 结果
     */
    public int deleteWfModelPermissionByModelPermissionId(String modelPermissionId);

    /**
     * 批量删除流程模型权限
     *
     * @param modelPermissionIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteWfModelPermissionByModelPermissionIds(String[] modelPermissionIds);

    /**
     * 批量插入流程模型权限
     *
     * @param permissionsList
     * @return
     */
    int insertWfModelPermissionList(List<WfModelPermission> permissionsList);

    /**
     * 根据模型ID删除流程模型权限
     *
     * @param modelId
     * @return
     */
    int deleteWfModelPermissionByModelId(String modelId);
}
