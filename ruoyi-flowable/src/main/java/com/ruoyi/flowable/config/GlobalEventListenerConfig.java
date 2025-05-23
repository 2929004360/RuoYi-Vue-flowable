package com.ruoyi.flowable.config;


import com.ruoyi.flowable.listener.GlobalEventListener;
import com.ruoyi.flowable.listener.TaskCreateListener;
import lombok.AllArgsConstructor;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEventDispatcher;
import org.flowable.engine.RuntimeService;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * flowable全局监听配置
 *
 * @author ssc
 */
@Configuration
@AllArgsConstructor
public class GlobalEventListenerConfig implements ApplicationListener<ContextRefreshedEvent> {

    private final GlobalEventListener globalEventListener;
    private final RuntimeService runtimeService;

    private final TaskCreateListener taskCreateListener;

    private final SpringProcessEngineConfiguration configuration;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        FlowableEventDispatcher dispatcher = configuration.getEventDispatcher();
        // 任务创建全局监听-待办消息发送
        dispatcher.addEventListener(taskCreateListener, FlowableEngineEventType.TASK_ASSIGNED);

        // 流程正常结束
        runtimeService.addEventListener(globalEventListener,
                FlowableEngineEventType.PROCESS_COMPLETED,
                FlowableEngineEventType.TASK_CREATED,
                FlowableEngineEventType.ACTIVITY_CANCELLED
        );

//                FlowableEngineEventType.TASK_CREATED,
//                FlowableEngineEventType.TASK_ASSIGNED,
    }
}
