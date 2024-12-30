package com.ruoyi.flowable.service;

import com.ruoyi.flowable.domain.WfFlowMenu;

import java.util.List;

/**
 * 流程菜单Service接口
 *
 * @author fengcheng
 * @date 2024-07-12
 */
public interface IWfFlowMenuService {
    /**
     * 查询流程菜单
     *
     * @param flowMenuId 流程菜单主键
     * @return 流程菜单
     */
    public WfFlowMenu selectWfFlowMenuByFlowMenuId(Long flowMenuId);

    /**
     * 查询流程菜单列表
     *
     * @param wfFlowMenu 流程菜单
     * @return 流程菜单集合
     */
    public List<WfFlowMenu> selectWfFlowMenuList(WfFlowMenu wfFlowMenu);

    /**
     * 新增流程菜单
     *
     * @param wfFlowMenu 流程菜单
     * @return 结果
     */
    public int insertWfFlowMenu(WfFlowMenu wfFlowMenu);

    /**
     * 修改流程菜单
     *
     * @param wfFlowMenu 流程菜单
     * @return 结果
     */
    public int updateWfFlowMenu(WfFlowMenu wfFlowMenu);

    /**
     * 批量删除流程菜单
     *
     * @param flowMenuIds 需要删除的流程菜单主键集合
     * @return 结果
     */
    public int deleteWfFlowMenuByFlowMenuIds(Long[] flowMenuIds);

    /**
     * 删除流程菜单信息
     *
     * @param flowMenuId 流程菜单主键
     * @return 结果
     */
    public int deleteWfFlowMenuByFlowMenuId(Long flowMenuId);

    /**
     * 获取流程菜单信息
     *
     * @param menuId
     * @return
     */
    WfFlowMenu getWfFlowMenuInfo(String menuId);
}
