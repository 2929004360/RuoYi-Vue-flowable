package com.ruoyi.flowable.service;


import com.ruoyi.common.core.domain.R;
import com.ruoyi.flowable.api.domain.bo.WfTaskBo;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 工作流任务接口
 *
 * @author fengcheng
 * @createTime 2022/3/10 00:12
 */
public interface IWfTaskService {
    /**
     * 审批任务
     *
     * @param task 请求实体参数
     */
    void complete(WfTaskBo task);

    /**
     * 驳回任务
     *
     * @param taskBo
     */
    void taskReject(WfTaskBo taskBo);


    /**
     * 退回任务
     *
     * @param bo 请求实体参数
     */
    void taskReturn(WfTaskBo bo);

    /**
     * 获取所有可回退的节点
     *
     * @param bo
     * @return
     */
    List<FlowElement> findReturnTaskList(WfTaskBo bo);

    /**
     * 删除任务
     *
     * @param bo 请求实体参数
     */
    void deleteTask(WfTaskBo bo);

    /**
     * 认领/签收任务
     *
     * @param bo 请求实体参数
     */
    void claim(WfTaskBo bo);

    /**
     * 取消认领/签收任务
     *
     * @param bo 请求实体参数
     */
    void unClaim(WfTaskBo bo);

    /**
     * 委派任务
     *
     * @param bo 请求实体参数
     */
    void delegateTask(WfTaskBo bo);

    /**
     * 转办任务
     *
     * @param bo 请求实体参数
     */
    void transferTask(WfTaskBo bo);

    /**
     * 取消申请
     *
     * @param bo
     * @return
     */
    void stopProcess(WfTaskBo bo);

    /**
     * 撤回流程
     *
     * @param bo
     * @return
     */
    void revokeProcess(WfTaskBo bo);

    /**
     * 获取流程过程图
     *
     * @param processId
     * @return
     */
    InputStream diagram(String processId);

    /**
     * 获取流程变量
     *
     * @param taskId 任务ID
     * @return 流程变量
     */
    Map<String, Object> getProcessVariables(String taskId);

    /**
     * 启动第一个任务
     *
     * @param processInstance 流程实例
     * @param variables 流程参数
     */
    void startFirstTask(ProcessInstance processInstance, Map<String, Object> variables);

    /**
     * 加签任务
     *
     * @param bo
     * @return
     */
    void addSignTask(WfTaskBo bo);

    /**
     * 多实例加签任务
     *
     * @param bo
     */
    void multiInstanceAddSign(WfTaskBo bo);

    /**
     * 收回流程,收回后发起人可以重新编辑表单发起流程，对于自定义业务就是原有任务都删除，重新进行申请
     *
     * @param bo
     * @return
     */
    R recallProcess(WfTaskBo bo);

    /**
     * 拒绝任务
     *
     * @param taskBo
     */
    void taskRefuse(WfTaskBo taskBo);

    /**
     * 跳转任务
     *
     * @param bo
     */
    void taskJump(WfTaskBo bo);

    /**
     * 用户任务列表,作为跳转任务使用
     *
     * @param bo
     * @return
     */
    R userTaskList(WfTaskBo bo);

    /**
     * 监听任务创建事件
     *
     * @param task 任务实体
     */
    void updateTaskStatusWhenCreated(Task task);
}
