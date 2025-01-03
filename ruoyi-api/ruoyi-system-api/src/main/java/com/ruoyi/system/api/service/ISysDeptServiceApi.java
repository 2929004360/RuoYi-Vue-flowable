package com.ruoyi.system.api.service;

import com.ruoyi.common.core.domain.entity.SysDept;

import java.util.List;

/**
 * 部门管理API 服务层
 *
 * @author fengcheng
 */
public interface ISysDeptServiceApi {

    /**
     * 根据部门ID查询信息
     *
     * @param deptId 部门ID
     * @return 部门信息
     */
    SysDept selectDeptById(Long deptId);

    /**
     * 获取下属部门
     *
     * @param deptId 部门ID
     * @return 部门信息id集合
     */
    List<Long> selectBranchDeptId(Long deptId);
}
