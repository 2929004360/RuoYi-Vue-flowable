package com.ruoyi.flowable.service.impl;

import com.ruoyi.flowable.domain.WfModelAssociationTemplate;
import com.ruoyi.flowable.mapper.WfModelAssociationTemplateMapper;
import com.ruoyi.flowable.service.IWfModelAssociationTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 模型关联模板Service业务层处理
 *
 * @author fengcheng
 * @date 2024-07-23
 */
@Service
public class WfModelAssociationTemplateServiceImpl implements IWfModelAssociationTemplateService
{
    @Autowired
    private WfModelAssociationTemplateMapper wfModelAssociationTemplateMapper;

    /**
     * 查询模型关联模板
     *
     * @param modelTemplateId 模型关联模板主键
     * @return 模型关联模板
     */
    @Override
    public WfModelAssociationTemplate selectWfModelAssociationTemplateByModelTemplateId(String modelTemplateId)
    {
        return wfModelAssociationTemplateMapper.selectWfModelAssociationTemplateByModelTemplateId(modelTemplateId);
    }

    /**
     * 查询模型关联模板列表
     *
     * @param wfModelAssociationTemplate 模型关联模板
     * @return 模型关联模板
     */
    @Override
    public List<WfModelAssociationTemplate> selectWfModelAssociationTemplateList(WfModelAssociationTemplate wfModelAssociationTemplate)
    {
        return wfModelAssociationTemplateMapper.selectWfModelAssociationTemplateList(wfModelAssociationTemplate);
    }

    /**
     * 新增模型关联模板
     *
     * @param wfModelAssociationTemplate 模型关联模板
     * @return 结果
     */
    @Override
    public int insertWfModelAssociationTemplate(WfModelAssociationTemplate wfModelAssociationTemplate)
    {
        return wfModelAssociationTemplateMapper.insertWfModelAssociationTemplate(wfModelAssociationTemplate);
    }

    /**
     * 修改模型关联模板
     *
     * @param wfModelAssociationTemplate 模型关联模板
     * @return 结果
     */
    @Override
    public int updateWfModelAssociationTemplate(WfModelAssociationTemplate wfModelAssociationTemplate)
    {
        return wfModelAssociationTemplateMapper.updateWfModelAssociationTemplate(wfModelAssociationTemplate);
    }

    /**
     * 批量删除模型关联模板
     *
     * @param modelTemplateIds 需要删除的模型关联模板主键
     * @return 结果
     */
    @Override
    public int deleteWfModelAssociationTemplateByModelTemplateIds(String[] modelTemplateIds)
    {
        return wfModelAssociationTemplateMapper.deleteWfModelAssociationTemplateByModelTemplateIds(modelTemplateIds);
    }

    /**
     * 删除模型关联模板信息
     *
     * @param modelTemplateId 模型关联模板主键
     * @return 结果
     */
    @Override
    public int deleteWfModelAssociationTemplateByModelTemplateId(String modelTemplateId)
    {
        return wfModelAssociationTemplateMapper.deleteWfModelAssociationTemplateByModelTemplateId(modelTemplateId);
    }

    /**
     * 删除模型关联模板信息
     *
     * @param wfModelAssociationTemplate
     */
    @Override
    public int deleteWfModelAssociationTemplate(WfModelAssociationTemplate wfModelAssociationTemplate) {
        return wfModelAssociationTemplateMapper.deleteWfModelAssociationTemplate(wfModelAssociationTemplate);
    }
}
