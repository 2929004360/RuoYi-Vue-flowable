package com.ruoyi.flowable.handler;

import cn.hutool.core.bean.BeanUtil;
import com.ruoyi.common.enums.FlowMenuEnum;
import com.ruoyi.common.enums.ProcessStatus;
import com.ruoyi.flowable.api.domain.WfBusinessProcess;
import com.ruoyi.flowable.api.domain.vo.WorkRiskVo;
import com.ruoyi.flowable.api.service.IWorkRiskServiceApi;
import com.ruoyi.flowable.service.IWfBusinessProcessService;
import com.ruoyi.flowable.service.IWfProcessService;
import org.flowable.engine.HistoryService;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * 重新发起流程处理类
 *
 * @author fengcheng
 */
@Component("resubmitProcessHandler")
public class ResubmitProcessHandler {

    @Autowired
    @Lazy
    private IWfBusinessProcessService wfBusinessProcessService;

    @Autowired
    @Lazy
    private IWfProcessService wfProcessService;

    @Autowired
    @Lazy
    protected HistoryService historyService;

    @Autowired
    @Lazy
    private IWorkRiskServiceApi workRiskServiceApi;

    /**
     * 重新发起流程
     *
     * @param wfBusinessProcess 重新发起
     */
    @Transactional(rollbackFor = Exception.class)
    public void resubmit(WfBusinessProcess wfBusinessProcess) {
        // 隐患流程
        if (FlowMenuEnum.RISK_FLOW_MENU.getCode().equals(wfBusinessProcess.getBusinessProcessType())) {
            updateRisk(wfBusinessProcess);
            return;
        }
    }


    /**
     * 修改隐患
     *
     * @param wfBusinessProcess 重新发起
     */
    private void updateRisk(WfBusinessProcess wfBusinessProcess) {
        // 删除流程实例
        List<String> ids = Collections.singletonList(wfBusinessProcess.getProcessId());
        // 删除历史流程实例
        historyService.bulkDeleteHistoricProcessInstances(ids);

        // 删除业务流程信息
        wfBusinessProcessService.deleteWfBusinessProcessByBusinessId(wfBusinessProcess.getBusinessId(), wfBusinessProcess.getBusinessProcessType());

        WorkRiskVo workRiskVo = workRiskServiceApi.selectWorkRiskByRiskId(wfBusinessProcess.getBusinessId());
        // 发起流程
        workRiskVo.setBusinessId(String.valueOf(workRiskVo.getRiskId()));
        workRiskVo.setBusinessProcessType(FlowMenuEnum.RISK_FLOW_MENU.getCode());
        ProcessInstance processInstance = wfProcessService.startProcessByDefId(workRiskVo.getDefinitionId(), BeanUtil.beanToMap(workRiskVo, new HashMap<>(16), false, false));
        String processInstanceId = processInstance.getProcessInstanceId();

        // 添加业务流程
        WfBusinessProcess process = new WfBusinessProcess();
        process.setProcessId(processInstanceId);
        process.setBusinessId(String.valueOf(workRiskVo.getRiskId()));
        process.setBusinessProcessType(FlowMenuEnum.RISK_FLOW_MENU.getCode());
        wfBusinessProcessService.insertWfBusinessProcess(process);

        // 修改对应流程
        WorkRiskVo workRisk = new WorkRiskVo();
        workRisk.setProcessId(processInstanceId);
        workRisk.setSchedule(ProcessStatus.RUNNING.getStatus());
        workRisk.setRiskId(workRiskVo.getRiskId());
        workRiskServiceApi.updateWorkRisk(workRisk);
    }
}
