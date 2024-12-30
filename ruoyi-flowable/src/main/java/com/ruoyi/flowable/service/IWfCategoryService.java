package com.ruoyi.flowable.service;

import com.ruoyi.flowable.domain.WfCategory;
import com.ruoyi.flowable.domain.vo.WfCategoryVo;
import com.ruoyi.flowable.page.PageQuery;
import com.ruoyi.flowable.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 流程分类Service接口
 *
 * @author fengcheng
 * @date 2022-01-15
 */
public interface IWfCategoryService {

    /**
     * 获取流程分类详细信息
     *
     * @param categoryId 分类主键
     * @return
     */
    WfCategoryVo queryById(String categoryId);

    /**
     * 查询流程分类列表
     *
     * @param category  流程分类对象
     * @param pageQuery 分页参数
     * @return
     */
    TableDataInfo<WfCategoryVo> queryPageList(WfCategory category, PageQuery pageQuery);

    /**
     * 查询全部的流程分类列表
     *
     * @param category 流程分类对象
     * @return
     */
    List<WfCategoryVo> queryList(WfCategory category);

    /**
     * 新增流程分类
     *
     * @param category 流程分类信息
     * @return 结果
     */
    int insertCategory(WfCategory category);

    /**
     * 修改流程分类
     *
     * @param category 流程分类信息
     * @return 结果
     */
    int updateCategory(WfCategory category);

    /**
     * 校验并删除数据
     *
     * @param ids 主键集合
     * @param isValid 是否校验,true-删除前校验,false-不校验
     * @return 结果
     */
    int deleteWithValidByIds(Collection<String> ids, Boolean isValid);

    /**
     * 校验分类编码是否唯一
     *
     * @param category 流程分类
     * @return 结果
     */
    boolean checkCategoryCodeUnique(WfCategory category);
}
