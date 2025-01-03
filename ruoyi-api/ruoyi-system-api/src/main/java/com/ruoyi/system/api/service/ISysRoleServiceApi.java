package com.ruoyi.system.api.service;

import com.ruoyi.common.core.domain.entity.SysRole;

/**
 * 角色API 业务层
 *
 * @author fengcheng
 */
public interface ISysRoleServiceApi {

    /**
     * 通过角色ID查询角色
     *
     * @param roleId 角色ID
     * @return 角色对象信息
     */
    SysRole selectRoleById(Long roleId);
}
