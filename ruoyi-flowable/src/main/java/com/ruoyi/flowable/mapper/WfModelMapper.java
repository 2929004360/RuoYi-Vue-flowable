package com.ruoyi.flowable.mapper;

import com.ruoyi.flowable.domain.ActReModel;
import com.ruoyi.flowable.domain.bo.WfModelBo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 流程模型Mapper接口
 *
 * @author fengcheng
 */
public interface WfModelMapper {

    /**
     * 查询流程模型列表
     *
     * @param modelBo
     * @param userIdList
     * @param deptIdList
     * @return
     */
    List<ActReModel> selectListVo(@Param("modelBo") WfModelBo modelBo, @Param("userIdList") List<Long> userIdList, @Param("deptIdList") List<Long> deptIdList);

    /**
     * 查询流程模型列表
     *
     * @param modelBo
     * @return
     */
    List<ActReModel> selectList(WfModelBo modelBo);
}
