package com.ruoyi.flowable.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.ruoyi.flowable.base.BaseMapperPlus;
import com.ruoyi.flowable.domain.WfForm;
import com.ruoyi.flowable.domain.bo.WfFormBo;
import com.ruoyi.flowable.domain.vo.WfFormVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 流程表单Mapper接口
 *
 * @author fengcheng
 * @createTime 2022/3/7 22:07
 */
public interface WfFormMapper extends BaseMapperPlus<WfFormMapper, WfForm, WfFormVo> {

    /**
     * 查询流程表单列表
     *
     * @param queryWrapper
     * @return
     */
    List<WfFormVo> selectFormVoList(@Param(Constants.WRAPPER) Wrapper<WfForm> queryWrapper);

    /**
     * 查询流程表单列表
     *
     * @param wfForm        流程表单
     * @param deptIdList    子部门id数据
     * @param ancestorsList 祖父部门id数据
     * @return 流程表单
     */
    List<WfFormVo> selectWfFormList(@Param("wfForm") WfFormBo wfForm, @Param("deptIdList") List<Long> deptIdList, @Param("ancestorsList") List<Long> ancestorsList);
}
