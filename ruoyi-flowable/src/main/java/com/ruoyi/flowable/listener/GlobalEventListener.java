package com.ruoyi.flowable.listener;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.enums.FlowMenuEnum;
import com.ruoyi.common.enums.ProcessStatus;
import com.ruoyi.flowable.api.domain.WfBusinessProcess;
import com.ruoyi.flowable.api.domain.vo.WorkLeaveVo;
import com.ruoyi.flowable.api.service.IWorkLeaveServiceApi;
import com.ruoyi.flowable.constant.ProcessConstants;
import com.ruoyi.flowable.constant.TaskConstants;
import com.ruoyi.flowable.domain.WfRoamHistorical;
import com.ruoyi.flowable.domain.vo.WfProcNodeVo;
import com.ruoyi.flowable.service.IWfBusinessProcessService;
import com.ruoyi.flowable.service.IWfRoamHistoricalService;
import com.ruoyi.flowable.service.IWfTaskService;
import com.ruoyi.flowable.utils.IdWorker;
import com.ruoyi.flowable.utils.StringUtils;
import com.ruoyi.system.api.service.ISysDeptServiceApi;
import com.ruoyi.system.api.service.ISysRoleServiceApi;
import com.ruoyi.system.api.service.ISysUserServiceApi;
import org.flowable.bpmn.constants.BpmnXMLConstants;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEntityEvent;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.delegate.event.AbstractFlowableEngineEventListener;
import org.flowable.engine.delegate.event.FlowableActivityCancelledEvent;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.task.Comment;
import org.flowable.identitylink.api.history.HistoricIdentityLink;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Flowable 全局监听器
 *
 * @author fengcheng
 * @since 2023/3/8 22:45
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class GlobalEventListener extends AbstractFlowableEngineEventListener {

    @Autowired
    @Lazy
    private IWfRoamHistoricalService wfRoamHistoricalService;

    @Autowired
    @Lazy
    private IdWorker idWorker;

    @Autowired
    @Lazy
    protected TaskService taskService;

    @Autowired
    @Lazy
    protected IWfTaskService wfTaskService;

    @Autowired
    @Lazy
    protected HistoryService historyService;

    @Lazy
    @Autowired
    private ISysUserServiceApi userServiceApi;

    @Lazy
    @Autowired
    private ISysRoleServiceApi roleServiceApi;

    @Lazy
    @Autowired
    private ISysDeptServiceApi deptServiceApi;

    @Autowired
    @Lazy
    private RuntimeService runtimeService;

    @Autowired
    @Lazy
    private IWfBusinessProcessService wfBusinessProcessService;

    @Autowired
    @Lazy
    private IWorkLeaveServiceApi workLeaveServiceApi;


    /**
     * 监听活动取消事件
     *
     * @param event
     */
    @Override
    protected void activityCancelled(FlowableActivityCancelledEvent event) {
        System.out.println("监听活动取消事件" + event.getActivityId());
    }

    /**
     * 监听任务创建事件
     *
     * @param event
     */
    @Override
    protected void taskCreated(FlowableEngineEntityEvent event) {
        System.out.println("监听任务创建事件" + event.getEntity());
        wfTaskService.updateTaskStatusWhenCreated((Task) event.getEntity());
    }

    /**
     * 流程结束监听器
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    protected void processCompleted(FlowableEngineEntityEvent event) {
        System.out.println("流程结束监听器" + event.getProcessInstanceId());
        String processInstanceId = event.getProcessInstanceId();
        Object variable = runtimeService.getVariable(processInstanceId, ProcessConstants.PROCESS_STATUS_KEY);
        ProcessStatus status = ProcessStatus.getProcessStatus(Convert.toStr(variable));

        if (ObjectUtil.isNotNull(status) && ProcessStatus.RUNNING == status) {
            runtimeService.setVariable(processInstanceId, ProcessConstants.PROCESS_STATUS_KEY, ProcessStatus.COMPLETED.getStatus());
        }
        // 修改业务流程
        try {
            updateBusiness(processInstanceId, status);
        } catch (Exception e) {
            throw new RuntimeException("修改业务流程失败" + e.getMessage());
        }
        super.processCompleted(event);
    }

    /**
     * 修改业务流程
     *
     * @param processInstanceId 流程id
     * @param status            流程状态
     */
    private void updateBusiness(String processInstanceId, ProcessStatus status) throws Exception {
        WfBusinessProcess wfBusinessProcess = wfBusinessProcessService.selectWfBusinessProcessByProcessId(processInstanceId);
        if (ObjectUtil.isNotNull(wfBusinessProcess)) {
            // 请假流程
            if (FlowMenuEnum.LEAVE_FLOW_MENU.getCode().equals(wfBusinessProcess.getBusinessProcessType())) {
                updateWorkLeave(wfBusinessProcess, status);
                return;
            }

        }
    }


    /**
     * 修改请假
     *
     * @param wfBusinessProcess 业务流程
     * @param status            流程状态
     */
    private void updateWorkLeave(WfBusinessProcess wfBusinessProcess, ProcessStatus status) throws Exception {
        String businessId = wfBusinessProcess.getBusinessId();
        WorkLeaveVo workLeaveVo = workLeaveServiceApi.selectWorkLeaveByLeaveId(businessId);

        workLeaveVo.setLeaveId(Long.valueOf(businessId));
        // 流程取消
        if (ProcessStatus.CANCELED.getStatus().equals(status.getStatus())) {
            workLeaveVo.setSchedule(ProcessStatus.CANCELED.getStatus());
        }

        // 流程终止
        if (ProcessStatus.TERMINATED.getStatus().equals(status.getStatus())) {
            workLeaveVo.setSchedule(ProcessStatus.TERMINATED.getStatus());
            // 插入流程流转历史
            insertWfRoamHistorical(wfBusinessProcess);
        }

        // 流程完成
        if (ProcessStatus.RUNNING.getStatus().equals(status.getStatus())) {
            workLeaveVo.setSchedule(ProcessStatus.COMPLETED.getStatus());
        }

        workLeaveServiceApi.updateWorkLeave(workLeaveVo);
    }

    /**
     * 插入流程流转历史
     *
     * @param wfBusinessProcess
     */
    private void insertWfRoamHistorical(WfBusinessProcess wfBusinessProcess) throws JsonProcessingException {
        // 获取流程实例
        HistoricProcessInstance historicProcIns = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(wfBusinessProcess.getProcessId()).includeProcessVariables().singleResult();
        List<WfProcNodeVo> wfProcNodeVos = historyProcNodeList(historicProcIns);
        wfRoamHistoricalService.insertWfRoamHistorical(
                new WfRoamHistorical(String.valueOf(idWorker.nextId()),
                        wfBusinessProcess.getBusinessId(),
                        wfBusinessProcess.getProcessId(),
                        wfBusinessProcess.getBusinessProcessType(),
                        new ObjectMapper().writeValueAsString(wfProcNodeVos))
        );
    }


    /**
     * 获取历史任务信息列表
     */
    private List<WfProcNodeVo> historyProcNodeList(HistoricProcessInstance historicProcIns) {
        String procInsId = historicProcIns.getId();
        List<HistoricActivityInstance> historicActivityInstanceList = historyService.createHistoricActivityInstanceQuery().processInstanceId(procInsId).activityTypes(CollUtil.newHashSet(BpmnXMLConstants.ELEMENT_EVENT_START, BpmnXMLConstants.ELEMENT_EVENT_END, BpmnXMLConstants.ELEMENT_TASK_USER)).orderByHistoricActivityInstanceStartTime().desc().orderByHistoricActivityInstanceEndTime().desc().list();
        List<Comment> commentList = taskService.getProcessInstanceComments(procInsId);
        List<WfProcNodeVo> elementVoList = new ArrayList<>();
        for (HistoricActivityInstance activityInstance : historicActivityInstanceList) {
            WfProcNodeVo elementVo = new WfProcNodeVo();
            elementVo.setProcDefId(activityInstance.getProcessDefinitionId());
            elementVo.setActivityId(activityInstance.getActivityId());
            elementVo.setActivityName(activityInstance.getActivityName());
            elementVo.setActivityType(activityInstance.getActivityType());
            elementVo.setCreateTime(activityInstance.getStartTime());
            elementVo.setEndTime(activityInstance.getEndTime());
            if (ObjectUtil.isNotNull(activityInstance.getDurationInMillis())) {
                elementVo.setDuration(DateUtil.formatBetween(activityInstance.getDurationInMillis(), BetweenFormatter.Level.SECOND));
            }
            if (BpmnXMLConstants.ELEMENT_EVENT_START.equals(activityInstance.getActivityType())) {
                if (ObjectUtil.isNotNull(historicProcIns)) {
                    Long userId = Long.parseLong(historicProcIns.getStartUserId());
                    SysUser sysUser = userServiceApi.selectUserById(userId);
                    String nickName = sysUser.getNickName();
                    if (nickName != null) {
                        elementVo.setAssigneeId(userId);
                        elementVo.setAssigneeName(nickName);
                    }
                }
            } else if (BpmnXMLConstants.ELEMENT_TASK_USER.equals(activityInstance.getActivityType())) {
                if (StringUtils.isNotBlank(activityInstance.getAssignee())) {
                    Long userId = Long.parseLong(activityInstance.getAssignee());
                    SysUser sysUser = userServiceApi.selectUserById(userId);
                    String nickName = sysUser.getNickName();
                    elementVo.setAssigneeId(userId);
                    elementVo.setAssigneeName(nickName);
                }
                // 展示审批人员
                List<HistoricIdentityLink> linksForTask = historyService.getHistoricIdentityLinksForTask(activityInstance.getTaskId());
                StringBuilder stringBuilder = new StringBuilder();
                for (HistoricIdentityLink identityLink : linksForTask) {
                    if ("candidate".equals(identityLink.getType())) {
                        if (StringUtils.isNotBlank(identityLink.getUserId())) {
                            Long userId = Long.parseLong(identityLink.getUserId());
                            SysUser sysUser = userServiceApi.selectUserById(userId);
                            String nickName = sysUser.getNickName();
                            stringBuilder.append(nickName).append(",");
                        }
                        if (StringUtils.isNotBlank(identityLink.getGroupId())) {
                            if (identityLink.getGroupId().startsWith(TaskConstants.ROLE_GROUP_PREFIX)) {
                                Long roleId = Long.parseLong(StringUtils.stripStart(identityLink.getGroupId(), TaskConstants.ROLE_GROUP_PREFIX));
                                SysRole role = roleServiceApi.selectRoleById(roleId);
                                stringBuilder.append(role.getRoleName()).append(",");
                            } else if (identityLink.getGroupId().startsWith(TaskConstants.DEPT_GROUP_PREFIX)) {
                                Long deptId = Long.parseLong(StringUtils.stripStart(identityLink.getGroupId(), TaskConstants.DEPT_GROUP_PREFIX));
                                SysDept dept = deptServiceApi.selectDeptById(deptId);
                                stringBuilder.append(dept.getDeptName()).append(",");
                            }
                        }
                    }
                }
                if (StringUtils.isNotBlank(stringBuilder)) {
                    elementVo.setCandidate(stringBuilder.substring(0, stringBuilder.length() - 1));
                }
                // 获取意见评论内容
                if (CollUtil.isNotEmpty(commentList)) {
                    List<Comment> comments = new ArrayList<>();
                    for (Comment comment : commentList) {
                        if (comment.getTaskId().equals(activityInstance.getTaskId())) {
                            comments.add(comment);
                        }
                    }
                    elementVo.setCommentList(comments);
                }
            }
            elementVoList.add(elementVo);
        }
        return elementVoList;
    }
}
