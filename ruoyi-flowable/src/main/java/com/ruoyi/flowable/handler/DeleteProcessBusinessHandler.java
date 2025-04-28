package com.ruoyi.flowable.handler;

import com.ruoyi.common.enums.FlowMenuEnum;
import com.ruoyi.common.enums.ProcessStatus;
import com.ruoyi.flowable.api.domain.WfBusinessProcess;
import com.ruoyi.flowable.api.domain.vo.WorkRiskVo;
import com.ruoyi.flowable.api.service.IWorkRiskServiceApi;
import com.ruoyi.flowable.service.IWfBusinessProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 删除业务流程处理类
 *
 * @author fengcheng
 */
@RequiredArgsConstructor
@Component("deleteProcessBusinessHandler")
public class DeleteProcessBusinessHandler {

    @Autowired
    @Lazy
    private IWfBusinessProcessService wfBusinessProcessService;

    @Autowired
    @Lazy
    private IWorkRiskServiceApi workRiskServiceApi;

    /**
     * 删除业务流程
     *
     * @param ids
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<String> ids) {
        List<WfBusinessProcess> list = wfBusinessProcessService.selectWfBusinessProcessListByProcessId(ids);

        for (WfBusinessProcess wfBusinessProcess : list) {
            // 隐患流程
            if (FlowMenuEnum.RISK_FLOW_MENU.getCode().equals(wfBusinessProcess.getBusinessProcessType())) {
                WorkRiskVo workRiskVo = new WorkRiskVo();
                workRiskVo.setRiskId(Long.valueOf(wfBusinessProcess.getBusinessId()));
                workRiskVo.setSchedule(ProcessStatus.UNAPPROVED.getStatus());
                workRiskServiceApi.updateWorkRisk(workRiskVo);
            }

            wfBusinessProcessService.deleteWfBusinessProcessByBusinessId(wfBusinessProcess.getBusinessId(), wfBusinessProcess.getBusinessProcessType());
        }
    }
}
