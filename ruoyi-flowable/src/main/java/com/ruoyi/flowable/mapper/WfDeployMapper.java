package com.ruoyi.flowable.mapper;

import com.ruoyi.flowable.core.domain.ProcessQuery;
import com.ruoyi.flowable.domain.Deploy;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WfDeployMapper {

    /**
     * 查询流程部署列表
     *
     * @param processQuery
     * @param procdefIdList
     * @return
     */
    List<Deploy> selectWfDeployList(@Param("processQuery") ProcessQuery processQuery, @Param("procdefIdList") List<String> procdefIdList);
}
