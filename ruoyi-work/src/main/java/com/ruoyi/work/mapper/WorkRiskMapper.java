package com.ruoyi.work.mapper;

import com.ruoyi.flowable.api.domain.WorkRisk;
import com.ruoyi.flowable.api.domain.vo.WorkRiskVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 隐患上报Mapper接口
 *
 * @author fengcheng
 * @date 2025-03-01
 */
public interface WorkRiskMapper {
    /**
     * 查询隐患上报
     *
     * @param riskId 隐患上报主键
     * @return 隐患上报
     */
    public WorkRiskVo selectWorkRiskByRiskId(Long riskId);

    /**
     * 查询隐患上报列表
     *
     * @param workLeaveVo 隐患上报
     * @param checked     是否包含下级
     * @return 隐患上报集合
     */
    public List<WorkRiskVo> selectWorkRiskList(@Param("workLeaveVo") WorkRiskVo workLeaveVo, @Param("checked") Boolean checked);

    /**
     * 新增隐患上报
     *
     * @param workRisk 隐患上报
     * @return 结果
     */
    public int insertWorkRisk(WorkRisk workRisk);

    /**
     * 修改隐患上报
     *
     * @param workRisk 隐患上报
     * @return 结果
     */
    public int updateWorkRisk(WorkRisk workRisk);

    /**
     * 删除隐患上报
     *
     * @param riskId 隐患上报主键
     * @return 结果
     */
    public int deleteWorkRiskByRiskId(Long riskId);

    /**
     * 批量删除隐患上报
     *
     * @param riskIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteWorkRiskByRiskIds(Long[] riskIds);
}
