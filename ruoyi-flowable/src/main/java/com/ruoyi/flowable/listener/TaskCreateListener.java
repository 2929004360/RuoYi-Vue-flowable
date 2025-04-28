package com.ruoyi.flowable.listener;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.flowable.subscription.PendingApprovalTemplate;
import com.ruoyi.flowable.utils.DateUtils;
import com.ruoyi.flowable.utils.RiskAreaUtils;
import com.ruoyi.system.api.service.ISysUserServiceApi;
import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.common.error.WxErrorException;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.common.engine.api.delegate.event.FlowableEventType;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

/**
 * 全局监听-工作流待办消息提醒
 *
 * @author fengcheng
 */
@Component
@RequiredArgsConstructor
public class TaskCreateListener implements FlowableEventListener {

    @Autowired
    @Lazy
    protected HistoryService historyService;

    @Autowired
    @Lazy
    protected RuntimeService runtimeService;

    @Lazy
    @Autowired
    private ISysUserServiceApi userServiceApi;

    @Lazy
    @Autowired
    private PendingApprovalTemplate pendingApprovalTemplate;

    @Lazy
    @Autowired
    protected TaskService taskService;

    /**
     * 监听类型
     *
     * @param flowableEvent
     */
    @Override
    public void onEvent(FlowableEvent flowableEvent) {
//        FlowableEventType type = flowableEvent.getType();
//        if (type == FlowableEngineEventType.TASK_ASSIGNED) {
//            System.out.println("任务分配事件");
//            if (flowableEvent instanceof org.flowable.engine.delegate.event.impl.FlowableEntityEventImpl) {
//                TaskEntity taskEntity = (TaskEntity) ((org.flowable.engine.delegate.event.impl.FlowableEntityEventImpl) flowableEvent).getEntity();
//                String procInsId = taskEntity.getProcessInstanceId();
//                HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(procInsId).singleResult();
//                String startUserId = historicProcessInstance.getStartUserId();
//
//                //获取任务接收人
//                String receiverUserId = taskEntity.getAssignee();
//
//                Map<String, Object> variables = taskService.getVariables(taskEntity.getId());
//
//                //发起人或登录人自己不发送
//                if (!(startUserId.equals(receiverUserId) && SecurityUtils.getUserId().toString().equals(receiverUserId))) {
//                    SysUser receiverUser = userServiceApi.selectUserById(Long.valueOf(receiverUserId));
//
//                    // 发送微信订阅消息
//                    try {
//                        pendingApprovalTemplate.sending(receiverUser.getOpenid(),
//                                variables.get("deptName").toString(),
//                                variables.get("userName").toString(),
//                                RiskAreaUtils.getHazardAreaNameByData(variables.get("riskArea").toString()),
//                                DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, (Date) variables.get("createTime")),
//                                "进行中"
//                        );
//                    } catch (WxErrorException e) {
//                        System.out.println(e.getMessage());
//                    }
//                }
//            }
//        }
    }

    @Override
    public boolean isFailOnException() {
        return false;
    }

    @Override
    public boolean isFireOnTransactionLifecycleEvent() {
        return false;
    }

    @Override
    public String getOnTransaction() {
        return null;
    }
}
