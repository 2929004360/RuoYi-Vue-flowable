package com.ruoyi.system.mapper;

import com.ruoyi.common.core.domain.entity.SysFactory;

import java.util.List;

/**
 * 厂区Mapper接口
 *
 * @author ruoyi
 * @date 2025-03-15
 */
public interface SysFactoryMapper
{
    /**
     * 查询厂区
     *
     * @param factoryId 厂区主键
     * @return 厂区
     */
    public SysFactory selectSysFactoryByFactoryId(Long factoryId);

    /**
     * 查询厂区列表
     *
     * @param sysFactory 厂区
     * @return 厂区集合
     */
    public List<SysFactory> selectSysFactoryList(SysFactory sysFactory);

    /**
     * 新增厂区
     *
     * @param sysFactory 厂区
     * @return 结果
     */
    public int insertSysFactory(SysFactory sysFactory);

    /**
     * 修改厂区
     *
     * @param sysFactory 厂区
     * @return 结果
     */
    public int updateSysFactory(SysFactory sysFactory);

    /**
     * 删除厂区
     *
     * @param factoryId 厂区主键
     * @return 结果
     */
    public int deleteSysFactoryByFactoryId(Long factoryId);

    /**
     * 批量删除厂区
     *
     * @param factoryIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysFactoryByFactoryIds(Long[] factoryIds);
}
