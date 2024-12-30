package com.ruoyi.flowable.mapper;

import com.ruoyi.flowable.domain.WfModelTemplate;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 模型模板Mapper接口
 *
 * @author fengcheng
 * @date 2024-07-17
 */
public interface WfModelTemplateMapper
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
     * 删除模型模板
     *
     * @param modelTemplateId 模型模板主键
     * @return 结果
     */
    public int deleteWfModelTemplateByModelTemplateId(String modelTemplateId);

    /**
     * 批量删除模型模板
     *
     * @param modelTemplateIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteWfModelTemplateByModelTemplateIds(String[] modelTemplateIds);

    /**
     * 查询模型模板列表
     *
     * @param wfModelTemplate 模型模板
     * @param deptIdList    子部门id数据
     * @param ancestorsList 祖父部门id数据
     * @return
     */
    List<WfModelTemplate> selectWfModelTemplateListVo(@Param("wfModelTemplate") WfModelTemplate wfModelTemplate,@Param("deptIdList") List<Long> deptIdList,@Param("ancestorsList") List<Long> ancestorsList);

}
