package com.ruoyi.flowable.service;

import com.ruoyi.flowable.domain.bo.WfFormBo;
import com.ruoyi.flowable.domain.vo.WfFormVo;

import java.util.Collection;
import java.util.List;

/**
 * 表单
 *
 * @author fengcheng
 * @createTime 2022/3/7 22:07
 */
public interface IWfFormService {
    /**
     * 查询流程表单
     *
     * @param formId 流程表单ID
     * @return 流程表单
     */
    WfFormVo queryById(String formId);

    /**
     * 查询流程表单列表
     *
     * @param bo 流程表单
     * @return 流程表单集合
     */
    List<WfFormVo> queryPageList(WfFormBo bo);

    /**
     * 查询流程表单列表
     *
     * @param bo 流程表单
     * @return 流程表单集合
     */
    List<WfFormVo> queryList(WfFormBo bo);

    /**
     * 新增流程表单
     *
     * @param bo 流程表单
     * @return 结果
     */
    int insertForm(WfFormBo bo);

    /**
     * 修改流程表单
     *
     * @param bo 流程表单
     * @return 结果
     */
    int updateForm(WfFormBo bo);

    /**
     * 批量删除流程表单
     *
     * @param formIds 需要删除的流程表单ID
     * @return 结果
     */
    Boolean deleteWithValidByIds(Collection<String> formIds);
}
