package com.ruoyi.system.api.impl;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.system.api.service.ISysUserServiceApi;
import com.ruoyi.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户API 服务实现
 *
 * @author fengcheng
 */
@Service
public class SysUserServiceApiImpl implements ISysUserServiceApi {

    @Autowired
    private ISysUserService userService;

    /**
     * 通过用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象信息
     */
    @Override
    public SysUser selectUserById(Long userId) {
        return userService.selectUserById(userId);
    }

    /**
     * 根据部门id获取下所有人的userId
     *
     * @param deptId 部门id
     * @return userId集合
     */
    @Override
    public List<Long> listUserIdByDeptId(Long deptId) {
        return userService.listUserIdByDeptId(deptId);
    }

    /**
     * 根据部门id获取下所有人的userId
     *
     * @param userIds 用户id集合
     * @return 用户列表
     */
    @Override
    public List<SysUser> selectSysUserByUserIdList(long[] userIds) {
        return userService.selectSysUserByUserIdList(userIds);
    }

    /**
     * 获取所有用户列表
     *
     * @return 用户列表
     */
    @Override
    public List<SysUser> getAllUser() {
        return userService.getAllUser();
    }

    /**
     * 根据角色id获取用户列表
     *
     * @param roleId 角色id
     * @return 用户列表
     */
    @Override
    public List<SysUser> getUserListByRoleId(String roleId) {
        return userService.getUserListByRoleId(roleId);
    }

    /**
     * 根据部门id获取用户列表
     *
     * @param deptId
     * @return
     */
    @Override
    public List<SysUser> getUserListByDeptId(long deptId) {
        return userService.getUserListByDeptId(deptId);
    }
}
