package com.ruoyi.flowable.handler;

import cn.hutool.core.bean.BeanUtil;
import com.ruoyi.common.enums.FlowMenuEnum;
import com.ruoyi.common.enums.ProcessStatus;
import com.ruoyi.flowable.api.domain.WfBusinessProcess;
import com.ruoyi.flowable.api.domain.vo.WorkLeaveVo;
import com.ruoyi.flowable.api.service.IWorkLeaveServiceApi;
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
    private IWorkLeaveServiceApi workLeaveServiceApi;

    /**
     * 重新发起流程
     *
     * @param wfBusinessProcess 重新发起
     */
    @Transactional(rollbackFor = Exception.class)
    public void resubmit(WfBusinessProcess wfBusinessProcess) {
        // 请假流程
        if (FlowMenuEnum.LEAVE_FLOW_MENU.getCode().equals(wfBusinessProcess.getBusinessProcessType())) {
            updateLeave(wfBusinessProcess);
            return;
        }
    }

    /**
     * 修改请假
     *
     * @param wfBusinessProcess 重新发起
     */
    private void updateLeave(WfBusinessProcess wfBusinessProcess) {
        // 删除流程实例
        List<String> ids = Collections.singletonList(wfBusinessProcess.getProcessId());
        // 删除历史流程实例
        historyService.bulkDeleteHistoricProcessInstances(ids);

        // 删除业务流程信息
        wfBusinessProcessService.deleteWfBusinessProcessByBusinessId(wfBusinessProcess.getBusinessId(), wfBusinessProcess.getBusinessProcessType());

        WorkLeaveVo workLeaveVo = workLeaveServiceApi.selectWorkLeaveByLeaveId(wfBusinessProcess.getBusinessId());
        // 发起流程
        workLeaveVo.setBusinessId(String.valueOf(workLeaveVo.getLeaveId()));
        workLeaveVo.setBusinessProcessType(FlowMenuEnum.LEAVE_FLOW_MENU.getCode());
        ProcessInstance processInstance = wfProcessService.startProcessByDefId(workLeaveVo.getDefinitionId(), BeanUtil.beanToMap(workLeaveVo, new HashMap<>(16), false, false));
        String processInstanceId = processInstance.getProcessInstanceId();

        // 添加业务流程
        WfBusinessProcess process = new WfBusinessProcess();
        process.setProcessId(processInstanceId);
        process.setBusinessId(String.valueOf(workLeaveVo.getLeaveId()));
        process.setBusinessProcessType(FlowMenuEnum.LEAVE_FLOW_MENU.getCode());
        wfBusinessProcessService.insertWfBusinessProcess(process);

        // 修改对应流程
        WorkLeaveVo workLeave = new WorkLeaveVo();
        workLeave.setProcessId(processInstanceId);
        workLeave.setSchedule(ProcessStatus.RUNNING.getStatus());
        workLeave.setLeaveId(workLeaveVo.getLeaveId());
        workLeaveServiceApi.updateWorkLeave(workLeave);
    }
}
