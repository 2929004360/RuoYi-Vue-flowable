package com.ruoyi.system.api.impl;

import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.system.api.service.ISysRoleServiceApi;
import com.ruoyi.system.service.ISysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 角色API 服务实现
 *
 * @author fengcheng
 */
@Service
public class SysRoleServiceApiImpl implements ISysRoleServiceApi {

    @Autowired
    private ISysRoleService roleService;

    /**
     * 通过角色ID查询角色
     *
     * @param roleId 角色ID
     * @return 角色对象信息
     */
    @Override
    public SysRole selectRoleById(Long roleId) {
        return roleService.selectRoleById(roleId);
    }
}
