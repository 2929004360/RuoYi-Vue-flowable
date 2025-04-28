package com.ruoyi.system.api.service;

import com.ruoyi.common.core.domain.entity.SysUser;

import java.util.List;

/**
 * 用户API 业务层
 *
 * @author fengcheng
 */
public interface ISysUserServiceApi {

    /**
     * 通过用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象信息
     */
    SysUser selectUserById(Long userId);

    /**
     * 根据部门id获取下所有人的userId
     *
     * @param deptId 部门id
     * @return userId集合
     */
    List<Long> listUserIdByDeptId(Long deptId);

    /**
     * 根据部门id获取下所有人的userId
     *
     * @param userIds 用户id集合
     * @return 用户列表
     */
    List<SysUser> selectSysUserByUserIdList(long[] userIds);

    /**
     * 获取所有用户列表
     *
     * @return 用户列表
     */
    List<SysUser> getAllUser();

    /**
     * 根据角色id获取用户列表
     *
     * @param roleId 角色id
     * @return 用户列表
     */
    List<SysUser> getUserListByRoleId(String roleId);

    /**
     * 根据部门id获取用户列表
     *
     * @param deptId
     * @return
     */
    List<SysUser> getUserListByDeptId(long deptId);
}
