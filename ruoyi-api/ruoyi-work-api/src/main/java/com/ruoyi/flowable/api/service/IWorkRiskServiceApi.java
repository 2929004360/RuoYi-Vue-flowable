package com.ruoyi.flowable.api.service;

import com.ruoyi.flowable.api.domain.vo.WorkRiskVo;

/**
 * 隐患上报api接口类
 *
 * @author fengcheng
 */
public interface IWorkRiskServiceApi {

    /**
     * 根据业务id查询请假单信息
     *
     * @param riskId
     * @return
     */
    WorkRiskVo selectWorkRiskByRiskId(String riskId);

    /**
     * 修改隐患上报
     *
     * @param workRiskVo
     */
    void updateWorkRisk(WorkRiskVo workRiskVo);
}
