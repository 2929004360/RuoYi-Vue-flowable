package com.ruoyi.work.api.impl;

import com.ruoyi.flowable.api.domain.vo.WorkRiskVo;
import com.ruoyi.flowable.api.service.IWorkRiskServiceApi;
import com.ruoyi.work.mapper.WorkRiskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 隐患上报api接口实现类
 *
 * @author fengcheng
 */
@RequiredArgsConstructor
@Service
public class IWorkRiskServiceApiImpl implements IWorkRiskServiceApi {

    @Autowired
    private WorkRiskMapper workRiskMapper;


    @Override
    public WorkRiskVo selectWorkRiskByRiskId(String riskId) {
        return workRiskMapper.selectWorkRiskByRiskId(Long.valueOf(riskId));
    }

    @Override
    public void updateWorkRisk(WorkRiskVo workRiskVo) {
        workRiskMapper.updateWorkRisk(workRiskVo);
    }
}
