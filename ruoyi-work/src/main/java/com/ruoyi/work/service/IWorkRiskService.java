package com.ruoyi.work.service;

import com.ruoyi.flowable.api.domain.vo.WorkRiskVo;

import java.util.List;

/**
 * 隐患上报Service接口
 *
 * @author fengcheng
 * @date 2025-03-01
 */
public interface IWorkRiskService {
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
    public List<WorkRiskVo> selectWorkRiskList(WorkRiskVo workLeaveVo,Boolean checked);

    /**
     * 新增隐患上报
     *
     * @param workRiskVo 隐患上报
     * @return 结果
     */
    public int insertWorkRisk(WorkRiskVo workRiskVo);

    /**
     * 修改隐患上报
     *
     * @param workRiskVo 隐患上报
     * @return 结果
     */
    public int updateWorkRisk(WorkRiskVo workRiskVo);

    /**
     * 批量删除隐患上报
     *
     * @param riskIds 需要删除的隐患上报主键集合
     * @return 结果
     */
    public int deleteWorkRiskByRiskIds(Long[] riskIds);

    /**
     * 删除隐患上报信息
     *
     * @param riskId 隐患上报主键
     * @return 结果
     */
    public int deleteWorkRiskByRiskId(Long riskId);

    /**
     * 提交审核
     *
     * @param riskId
     * @return
     */
    int submit(Long riskId);

    /**
     * 撤销审核
     *
     * @param riskId
     * @return
     */
    int revoke(Long riskId);

    /**
     * 重新申请
     *
     * @param riskId
     * @return
     */
    int reapply(Long riskId);
}
