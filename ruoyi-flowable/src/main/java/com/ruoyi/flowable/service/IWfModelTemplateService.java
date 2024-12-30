package com.ruoyi.flowable.service;

import com.ruoyi.flowable.domain.WfModelTemplate;

import java.util.List;

/**
 * 模型模板Service接口
 *
 * @author fengcheng
 * @date 2024-07-17
 */
public interface IWfModelTemplateService
{
    /**
     * 查询模型模板
     *
     * @param modelTemplateId 模型模板主键
     * @return 模型模板
     */
    public WfModelTemplate selectWfModelTemplateByModelTemplateId(String modelTemplateId);

    /**
     * 查询模型模板列表
     *
     * @param wfModelTemplate 模型模板
     * @return 模型模板集合
     */
    public List<WfModelTemplate> selectWfModelTemplateList(WfModelTemplate wfModelTemplate);

    /**
     * 新增模型模板
     *
     * @param wfModelTemplate 模型模板
     * @return 结果
     */
    public int insertWfModelTemplate(WfModelTemplate wfModelTemplate);

    /**
     * 修改模型模板
     *
     * @param wfModelTemplate 模型模板
     * @return 结果
     */
    public int updateWfModelTemplate(WfModelTemplate wfModelTemplate);

    /**
     * 批量删除模型模板
     *
     * @param modelTemplateIds 需要删除的模型模板主键集合
     * @return 结果
     */
    public int deleteWfModelTemplateByModelTemplateIds(String[] modelTemplateIds);

    /**
     * 删除模型模板信息
     *
     * @param modelTemplateId 模型模板主键
     * @return 结果
     */
    public int deleteWfModelTemplateByModelTemplateId(String modelTemplateId);

    /**
     * 修改模型模板xml
     *
     * @param wfModelTemplate 模型模板
     * @return 结果
     */
    int editBpmnXml(WfModelTemplate wfModelTemplate);
}
