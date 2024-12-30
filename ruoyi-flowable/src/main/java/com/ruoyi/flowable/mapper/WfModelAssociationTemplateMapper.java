package com.ruoyi.flowable.mapper;

import com.ruoyi.flowable.domain.WfModelAssociationTemplate;

import java.util.List;

/**
 * 模型关联模板Mapper接口
 *
 * @author fengcheng
 * @date 2024-07-23
 */
public interface WfModelAssociationTemplateMapper
{
    /**
     * 查询模型关联模板
     *
     * @param modelTemplateId 模型关联模板主键
     * @return 模型关联模板
     */
    public WfModelAssociationTemplate selectWfModelAssociationTemplateByModelTemplateId(String modelTemplateId);

    /**
     * 查询模型关联模板列表
     *
     * @param wfModelAssociationTemplate 模型关联模板
     * @return 模型关联模板集合
     */
    public List<WfModelAssociationTemplate> selectWfModelAssociationTemplateList(WfModelAssociationTemplate wfModelAssociationTemplate);

    /**
     * 新增模型关联模板
     *
     * @param wfModelAssociationTemplate 模型关联模板
     * @return 结果
     */
    public int insertWfModelAssociationTemplate(WfModelAssociationTemplate wfModelAssociationTemplate);

    /**
     * 修改模型关联模板
     *
     * @param wfModelAssociationTemplate 模型关联模板
     * @return 结果
     */
    public int updateWfModelAssociationTemplate(WfModelAssociationTemplate wfModelAssociationTemplate);

    /**
     * 删除模型关联模板
     *
     * @param modelTemplateId 模型关联模板主键
     * @return 结果
     */
    public int deleteWfModelAssociationTemplateByModelTemplateId(String modelTemplateId);

    /**
     * 批量删除模型关联模板
     *
     * @param modelTemplateIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteWfModelAssociationTemplateByModelTemplateIds(String[] modelTemplateIds);

    /**
     * 删除模型关联模板信息
     *
     * @param wfModelAssociationTemplate
     */
    int deleteWfModelAssociationTemplate(WfModelAssociationTemplate wfModelAssociationTemplate);
}
