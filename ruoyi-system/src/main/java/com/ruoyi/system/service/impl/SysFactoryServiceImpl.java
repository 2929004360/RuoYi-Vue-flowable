package com.ruoyi.system.service.impl;

import java.util.List;

import com.ruoyi.common.core.domain.entity.SysFactory;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.SysFactoryMapper;
import com.ruoyi.system.service.ISysFactoryService;

/**
 * 厂区Service业务层处理
 *
 * @author ruoyi
 * @date 2025-03-15
 */
@Service
public class SysFactoryServiceImpl implements ISysFactoryService
{
    @Autowired
    private SysFactoryMapper sysFactoryMapper;

    /**
     * 查询厂区
     *
     * @param factoryId 厂区主键
     * @return 厂区
     */
    @Override
    public SysFactory selectSysFactoryByFactoryId(Long factoryId)
    {
        return sysFactoryMapper.selectSysFactoryByFactoryId(factoryId);
    }

    /**
     * 查询厂区列表
     *
     * @param sysFactory 厂区
     * @return 厂区
     */
    @Override
    public List<SysFactory> selectSysFactoryList(SysFactory sysFactory)
    {
        return sysFactoryMapper.selectSysFactoryList(sysFactory);
    }

    /**
     * 新增厂区
     *
     * @param sysFactory 厂区
     * @return 结果
     */
    @Override
    public int insertSysFactory(SysFactory sysFactory)
    {
        sysFactory.setCreateTime(DateUtils.getNowDate());
        return sysFactoryMapper.insertSysFactory(sysFactory);
    }

    /**
     * 修改厂区
     *
     * @param sysFactory 厂区
     * @return 结果
     */
    @Override
    public int updateSysFactory(SysFactory sysFactory)
    {
        sysFactory.setUpdateTime(DateUtils.getNowDate());
        return sysFactoryMapper.updateSysFactory(sysFactory);
    }

    /**
     * 批量删除厂区
     *
     * @param factoryIds 需要删除的厂区主键
     * @return 结果
     */
    @Override
    public int deleteSysFactoryByFactoryIds(Long[] factoryIds)
    {
        return sysFactoryMapper.deleteSysFactoryByFactoryIds(factoryIds);
    }

    /**
     * 删除厂区信息
     *
     * @param factoryId 厂区主键
     * @return 结果
     */
    @Override
    public int deleteSysFactoryByFactoryId(Long factoryId)
    {
        return sysFactoryMapper.deleteSysFactoryByFactoryId(factoryId);
    }
}
