package com.ruoyi.flowable.listener;

import lombok.RequiredArgsConstructor;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.common.engine.api.delegate.event.FlowableEventType;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

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

    @Override
    public void onEvent(FlowableEvent flowableEvent) {
        FlowableEventType type = flowableEvent.getType();
        if (type == FlowableEngineEventType.TASK_ASSIGNED) {
            System.out.println("任务分配事件");
        }
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
