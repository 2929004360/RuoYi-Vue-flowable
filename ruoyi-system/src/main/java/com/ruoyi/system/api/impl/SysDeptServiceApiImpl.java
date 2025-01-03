package com.ruoyi.system.api.impl;

import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.system.api.service.ISysDeptServiceApi;
import com.ruoyi.system.service.ISysDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 部门管理API 服务实现
 *
 * @author fengcheng
 */
@Service
public class SysDeptServiceApiImpl implements ISysDeptServiceApi {

    @Autowired
    private ISysDeptService deptService;

    /**
     * 根据部门ID查询信息
     *
     * @param deptId 部门ID
     * @return 部门信息
     */
    @Override
    public SysDept selectDeptById(Long deptId) {
        return deptService.selectDeptById(deptId);
    }

    /**
     * 获取下属部门
     *
     * @param deptId 部门ID
     * @return 部门信息id集合
     */
    @Override
    public List<Long> selectBranchDeptId(Long deptId) {
        return deptService.selectBranchDeptId(deptId);
    }
}
