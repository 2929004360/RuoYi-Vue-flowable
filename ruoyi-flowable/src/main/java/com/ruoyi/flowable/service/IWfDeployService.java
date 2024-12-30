package com.ruoyi.flowable.service;

import com.ruoyi.flowable.core.domain.ProcessQuery;
import com.ruoyi.flowable.domain.vo.WfDeployVo;
import com.ruoyi.flowable.page.PageQuery;
import com.ruoyi.flowable.page.TableDataInfo;

import java.util.List;

/**
 * @author fengcheng
 * @createTime 2022/6/30 9:03
 */
public interface IWfDeployService {

    TableDataInfo<WfDeployVo> queryPageList(ProcessQuery processQuery, PageQuery pageQuery);

    TableDataInfo<WfDeployVo> queryPublishList(String processKey, PageQuery pageQuery);

    void updateState(String definitionId, String stateCode);

    String queryBpmnXmlById(String definitionId);

    void deleteByIds(List<String> deployIds);

    /**
     * 查询流程部署列表
     *
     * @param processQuery
     * @param procdefIdList
     * @return
     */
    List<WfDeployVo> selectWfDeployList(ProcessQuery processQuery, List<String> procdefIdList);
}
