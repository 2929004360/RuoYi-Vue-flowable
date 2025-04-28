package com.ruoyi.flowable.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.flowable.domain.WfFlowMenu;
import com.ruoyi.flowable.mapper.WfFlowMenuMapper;
import com.ruoyi.flowable.service.IWfFlowMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 流程菜单Service业务层处理
 *
 * @author fengcheng
 * @date 2024-07-12
 */
@Service
public class WfFlowMenuServiceImpl implements IWfFlowMenuService {
    @Autowired
    private WfFlowMenuMapper wfFlowMenuMapper;

    /**
     * 查询流程菜单
     *
     * @param flowMenuId 流程菜单主键
     * @return 流程菜单
     */
    @Override
    public WfFlowMenu selectWfFlowMenuByFlowMenuId(String flowMenuId) {
        return wfFlowMenuMapper.selectWfFlowMenuByFlowMenuId(flowMenuId);
    }

    /**
     * 查询流程菜单列表
     *
     * @param wfFlowMenu 流程菜单
     * @return 流程菜单
     */
    @Override
    public List<WfFlowMenu> selectWfFlowMenuList(WfFlowMenu wfFlowMenu) {
        return wfFlowMenuMapper.selectWfFlowMenuList(wfFlowMenu);
    }

    /**
     * 新增流程菜单
     *
     * @param wfFlowMenu 流程菜单
     * @return 结果
     */
    @Override
    public int insertWfFlowMenu(WfFlowMenu wfFlowMenu) {
        WfFlowMenu menu = new WfFlowMenu();
        menu.setMenuId(wfFlowMenu.getMenuId());
        List<WfFlowMenu> wfFlowMenus = wfFlowMenuMapper.selectWfFlowMenuList(menu);
        if (wfFlowMenus.size() > 0) {
            throw new RuntimeException("菜单已存在" );
        }
        wfFlowMenu.setCreateTime(DateUtils.getNowDate());
        return wfFlowMenuMapper.insertWfFlowMenu(wfFlowMenu);
    }

    /**
     * 修改流程菜单
     *
     * @param wfFlowMenu 流程菜单
     * @return 结果
     */
    @Override
    public int updateWfFlowMenu(WfFlowMenu wfFlowMenu) {
        wfFlowMenu.setUpdateTime(DateUtils.getNowDate());
        return wfFlowMenuMapper.updateWfFlowMenu(wfFlowMenu);
    }

    /**
     * 批量删除流程菜单
     *
     * @param flowMenuIds 需要删除的流程菜单主键
     * @return 结果
     */
    @Override
    public int deleteWfFlowMenuByFlowMenuIds(Long[] flowMenuIds) {
        return wfFlowMenuMapper.deleteWfFlowMenuByFlowMenuIds(flowMenuIds);
    }

    /**
     * 删除流程菜单信息
     *
     * @param flowMenuId 流程菜单主键
     * @return 结果
     */
    @Override
    public int deleteWfFlowMenuByFlowMenuId(Long flowMenuId) {
        return wfFlowMenuMapper.deleteWfFlowMenuByFlowMenuId(flowMenuId);
    }

    /**
     * 获取流程菜单信息
     *
     * @param menuId
     * @return
     */
    @Override
    public WfFlowMenu getWfFlowMenuInfo(String menuId) {
        return wfFlowMenuMapper.getWfFlowMenuInfo(menuId);
    }
}
