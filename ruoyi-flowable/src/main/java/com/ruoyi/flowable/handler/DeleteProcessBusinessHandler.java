package com.ruoyi.flowable.handler;

import com.ruoyi.common.enums.FlowMenuEnum;
import com.ruoyi.common.enums.ProcessStatus;
import com.ruoyi.flowable.api.domain.WfBusinessProcess;
import com.ruoyi.flowable.api.domain.vo.WorkLeaveVo;
import com.ruoyi.flowable.api.service.IWorkLeaveServiceApi;
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
    private IWorkLeaveServiceApi workLeaveServiceApi;

    /**
     * 删除业务流程
     *
     * @param ids
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<String> ids) {
        List<WfBusinessProcess> list = wfBusinessProcessService.selectWfBusinessProcessListByProcessId(ids);

        for (WfBusinessProcess wfBusinessProcess : list) {
            // 请假流程
            if (FlowMenuEnum.LEAVE_FLOW_MENU.getCode().equals(wfBusinessProcess.getBusinessProcessType())) {
                WorkLeaveVo workLeaveVo = new WorkLeaveVo();
                workLeaveVo.setLeaveId(Long.valueOf(wfBusinessProcess.getBusinessId()));
                workLeaveVo.setSchedule(ProcessStatus.UNAPPROVED.getStatus());
                workLeaveServiceApi.updateWorkLeave(workLeaveVo);
            }

            wfBusinessProcessService.deleteWfBusinessProcessByBusinessId(wfBusinessProcess.getBusinessId(), wfBusinessProcess.getBusinessProcessType());
        }
    }
}
