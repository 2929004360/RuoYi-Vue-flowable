package com.ruoyi.flowable.handler;

import cn.hutool.core.bean.BeanUtil;
import com.ruoyi.common.enums.FlowMenuEnum;
import com.ruoyi.flowable.api.service.IWorkLeaveServiceApi;
import com.ruoyi.flowable.constant.ProcessConstants;
import com.ruoyi.flowable.domain.vo.WfDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 业务流程详情处理类
 *
 * @author fengcheng
 */
@Component("businessProcessDetailsHandler")
public class BusinessProcessDetailsHandler {

    @Autowired
    @Lazy
    private IWorkLeaveServiceApi workLeaveServiceApi;

    /**
     * 设置业务流程
     *
     * @param detailVo
     * @param processVariables
     */
    public void setBusinessProcess(WfDetailVo detailVo, Map<String, Object> processVariables) {
        // 流程状态
//        String processStatus = processVariables.get(ProcessConstants.PROCESS_STATUS_KEY).toString();
        // 业务ID
        String businessId = processVariables.get(ProcessConstants.BUSINESS_ID).toString();
        // 启动流程的人
//        String initiator = processVariables.get(BpmnXMLConstants.ATTRIBUTE_EVENT_START_INITIATOR).toString();
        // 业务流程类型
        String businessProcessType = processVariables.get(ProcessConstants.BUSINESS_PROCESS_TYPE).toString();

        // 请假和销假流程
        if (FlowMenuEnum.LEAVE_FLOW_MENU.getCode().equals(businessProcessType)) {
            detailVo.setBusinessProcess(BeanUtil.beanToMap(workLeaveServiceApi.selectWorkLeaveByLeaveId(businessId), new HashMap<>(16), false, false));
            return;
        }
    }
}
