package com.ruoyi.flowable.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.enums.ProcessStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.flowable.api.domain.bo.WfTaskBo;
import com.ruoyi.flowable.constant.ProcessConstants;
import com.ruoyi.flowable.constant.TaskConstants;
import com.ruoyi.flowable.domain.msg.MessageSendWhenTaskCreatedReq;
import com.ruoyi.flowable.domain.vo.WfUserTaskVo;
import com.ruoyi.flowable.enums.FlowComment;
import com.ruoyi.flowable.factory.FlowServiceFactory;
import com.ruoyi.flowable.flow.CustomProcessDiagramGenerator;
import com.ruoyi.flowable.flow.FlowableUtils;
import com.ruoyi.flowable.mapper.FlowTaskMapper;
import com.ruoyi.flowable.service.IWfCopyService;
import com.ruoyi.flowable.service.IWfTaskService;
import com.ruoyi.flowable.utils.ModelUtils;
import com.ruoyi.flowable.utils.NumberUtils;
import com.ruoyi.flowable.utils.StringUtils;
import com.ruoyi.flowable.utils.TaskUtils;
import com.ruoyi.system.api.service.ISysUserServiceApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.flowable.bpmn.constants.BpmnXMLConstants;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.common.engine.api.FlowableObjectNotFoundException;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.flowable.engine.impl.bpmn.behavior.SequentialMultiInstanceBehavior;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ChangeActivityStateBuilder;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.identitylink.api.IdentityLinkType;
import org.flowable.image.ProcessDiagramGenerator;
import org.flowable.task.api.DelegationState;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.flowable.task.service.impl.persistence.entity.TaskEntityImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author fengcheng
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class WfTaskServiceImpl extends FlowServiceFactory implements IWfTaskService {

    private final ISysUserServiceApi userServiceApi;

    private final IWfCopyService copyService;

    private final FlowTaskMapper flowTaskMapper;

//    private final RemoteSysMessageService remoteSysMessageService;

    /**
     * 完成任务
     *
     * @param taskBo 请求实体参数
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void complete(WfTaskBo taskBo) {
        Task task = taskService.createTaskQuery().taskId(taskBo.getTaskId()).singleResult();
        TaskEntity taskEntity = (TaskEntity) taskService.createTaskQuery().taskId(taskBo.getTaskId()).singleResult();
        if (Objects.isNull(task)) {
            throw new ServiceException("任务不存在");
        }
        // 获取 bpmn 模型
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        if (DelegationState.PENDING.equals(task.getDelegationState())) {
            if (ObjectUtil.isNotNull(taskBo.getParentTaskId()) && !StrUtil.isEmpty(taskBo.getParentTaskId())) {
                taskService.addComment(taskBo.getParentTaskId(), taskBo.getProcInsId(), FlowComment.DELEGATE.getType(), SecurityUtils.getLoginUser().getUser().getNickName() + "在[" + task.getName() + "]节点加签委派原因:" + taskBo.getComment());
            } else {
                taskService.addComment(taskBo.getTaskId(), taskBo.getProcInsId(), FlowComment.DELEGATE.getType(), taskBo.getComment());
            }
            taskService.resolveTask(taskBo.getTaskId());
        } else {
            if (ObjectUtil.isNotNull(taskBo.getParentTaskId()) && !StrUtil.isEmpty(taskBo.getParentTaskId())) {
                taskService.addComment(taskBo.getParentTaskId(), taskBo.getProcInsId(), FlowComment.NORMAL.getType(), SecurityUtils.getLoginUser().getUser().getNickName() + "在[" + task.getName() + "]节点加签审批通过原因:" + taskBo.getComment());
            } else {
                taskService.addComment(taskBo.getTaskId(), taskBo.getProcInsId(), FlowComment.NORMAL.getType(), taskBo.getComment());
            }

            taskService.setAssignee(taskBo.getTaskId(), String.valueOf(SecurityUtils.getUserId()));
            if (ObjectUtil.isNotEmpty(taskBo.getVariables())) {
                // 获取模型信息
                String localScopeValue = ModelUtils.getUserTaskAttributeValue(bpmnModel, task.getTaskDefinitionKey(), ProcessConstants.PROCESS_FORM_LOCAL_SCOPE);
                boolean localScope = Convert.toBool(localScopeValue, false);
                taskService.complete(taskBo.getTaskId(), taskBo.getVariables(), localScope);
            } else {
                taskService.complete(taskBo.getTaskId());
            }
        }
        // 设置任务节点名称
        taskBo.setTaskName(task.getName());

        // 处理下一级接收人
        if (ObjectUtil.isNotEmpty(taskBo.getNextApproval())) {
            this.assignNextApproval(bpmnModel, taskBo.getProcInsId(), taskBo.getNextApproval());
        }

        // 处理下一级审批人
        if (StringUtils.isNotBlank(taskBo.getNextUserIds())) {
            this.assignNextUsers(bpmnModel, taskBo.getProcInsId(), taskBo.getNextUserIds());
        }

        //加签处理
        addSignForComplete(taskBo, taskEntity);

        // 处理抄送用户
        if (!copyService.makeCopy(taskBo)) {
            throw new RuntimeException("抄送任务失败");
        }
    }

    /**
     * 指派下一任务接收人
     *
     * @param bpmnModel    bpmn模型
     * @param processInsId 流程实例id
     * @param userIds      用户ids 这个实际上换成userNames了
     */
    private void assignNextApproval(BpmnModel bpmnModel, String processInsId, String userIds) {
        // 获取所有节点信息
        List<Task> list = taskService.createTaskQuery().processInstanceId(processInsId).list();
        if (list.size() == 0) {
            return;
        }
        Queue<String> assignIds = CollUtil.newLinkedList(userIds.split(","));
        if (list.size() == assignIds.size()) {
            for (Task task : list) {
                taskService.setAssignee(task.getId(), assignIds.poll());
            }
            return;
        }
    }

    /**
     * 流程审批处理加签任务
     *
     * @param taskVo
     * @param taskEntity
     */
    void addSignForComplete(WfTaskBo taskVo, TaskEntity taskEntity) {
        //查看当前任务是存在
        if (taskEntity == null) {
            throw new FlowableException("该任务id对应任务不存在！");
        }
        //处理加签父任务
        String parentTaskId = taskEntity.getParentTaskId();
        if (StringUtils.isNotBlank(parentTaskId)) {
            int subTaskCount = flowTaskMapper.querySubTaskByParentTaskId(parentTaskId);
            //如果没有其他子任务
            if (subTaskCount == 0) {
                Task task = processEngine.getTaskService().createTaskQuery().taskId(parentTaskId).singleResult();

                //处理前后加签的任务
                processEngine.getTaskService().resolveTask(parentTaskId);
                if ("after".equals(task.getScopeType())) {
                    processEngine.getTaskService().complete(parentTaskId);
                }
            }
        }
    }

    /**
     * 驳回任务
     *
     * @param taskBo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void taskReject(WfTaskBo taskBo) {
        // 当前任务 task
        Task task = taskService.createTaskQuery().taskId(taskBo.getTaskId()).singleResult();
        if (ObjectUtil.isNull(task)) {
            throw new RuntimeException("获取任务信息异常！");
        }
        if (task.isSuspended()) {
            throw new RuntimeException("任务处于挂起状态");
        }
        // 获取流程实例
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(taskBo.getProcInsId()).singleResult();
        if (processInstance == null) {
            throw new RuntimeException("流程实例不存在，请确认！");
        }
        // 获取流程定义信息
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(task.getProcessDefinitionId()).singleResult();
        // 获取所有节点信息
        Process process = repositoryService.getBpmnModel(processDefinition.getId()).getProcesses().get(0);
        // 获取全部节点列表，包含子节点
        Collection<FlowElement> allElements = FlowableUtils.getAllElements(process.getFlowElements(), null);
        // 获取当前任务节点元素
        FlowElement source = null;
        if (allElements != null) {
            for (FlowElement flowElement : allElements) {
                // 类型为用户节点
                if (flowElement.getId().equals(task.getTaskDefinitionKey())) {
                    // 获取节点信息
                    source = flowElement;
                }
            }
        }

        // 目的获取所有跳转到的节点 targetIds
        // 获取当前节点的所有父级用户任务节点
        // 深度优先算法思想：延边迭代深入
        List<UserTask> parentUserTaskList = FlowableUtils.iteratorFindParentUserTasks(source, null, null);
        if (parentUserTaskList == null || parentUserTaskList.size() == 0) {
            throw new RuntimeException("当前节点为初始任务节点，不能驳回");
        }
        // 获取活动 ID 即节点 Key
        List<String> parentUserTaskKeyList = new ArrayList<>();
        parentUserTaskList.forEach(item -> parentUserTaskKeyList.add(item.getId()));
        // 获取全部历史节点活动实例，即已经走过的节点历史，数据采用开始时间升序
        List<HistoricTaskInstance> historicTaskInstanceList = historyService.createHistoricTaskInstanceQuery().processInstanceId(task.getProcessInstanceId()).orderByHistoricTaskInstanceStartTime().asc().list();
        // 数据清洗，将回滚导致的脏数据清洗掉
        List<String> lastHistoricTaskInstanceList = null;
        if (allElements != null) {
            lastHistoricTaskInstanceList = FlowableUtils.historicTaskInstanceClean(allElements, historicTaskInstanceList);
        }
        // 此时历史任务实例为倒序，获取最后走的节点
        List<String> targetIds = new ArrayList<>();
        // 循环结束标识，遇到当前目标节点的次数
        int number = 0;
        StringBuilder parentHistoricTaskKey = new StringBuilder();
        for (String historicTaskInstanceKey : lastHistoricTaskInstanceList) {
            // 当会签时候会出现特殊的，连续都是同一个节点历史数据的情况，这种时候跳过
            if (parentHistoricTaskKey.toString().equals(historicTaskInstanceKey)) {
                continue;
            }
            parentHistoricTaskKey = new StringBuilder(historicTaskInstanceKey);
            if (historicTaskInstanceKey.equals(task.getTaskDefinitionKey())) {
                number++;
            }
            // 在数据清洗后，历史节点就是唯一一条从起始到当前节点的历史记录，理论上每个点只会出现一次
            // 在流程中如果出现循环，那么每次循环中间的点也只会出现一次，再出现就是下次循环
            // number == 1，第一次遇到当前节点
            // number == 2，第二次遇到，代表最后一次的循环范围
            if (number == 2) {
                break;
            }
            // 如果当前历史节点，属于父级的节点，说明最后一次经过了这个点，需要退回这个点
            if (parentUserTaskKeyList.contains(historicTaskInstanceKey)) {
                targetIds.add(historicTaskInstanceKey);
            }
        }


        // 目的获取所有需要被跳转的节点 currentIds
        // 取其中一个父级任务，因为后续要么存在公共网关，要么就是串行公共线路
        UserTask oneUserTask = parentUserTaskList.get(0);
        // 获取所有正常进行的任务节点 Key，这些任务不能直接使用，需要找出其中需要驳回的任务
        List<Task> runTaskList = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).list();
        List<String> runTaskKeyList = new ArrayList<>();
        runTaskList.forEach(item -> runTaskKeyList.add(item.getTaskDefinitionKey()));
        // 需驳回任务列表
        List<String> currentIds = new ArrayList<>();
        // 通过父级网关的出口连线，结合 runTaskList 比对，获取需要驳回的任务
        List<UserTask> currentUserTaskList = FlowableUtils.iteratorFindChildUserTasks(oneUserTask, runTaskKeyList, null, null);
        currentUserTaskList.forEach(item -> currentIds.add(item.getId()));


        // 规定：并行网关之前节点必须需存在唯一用户任务节点，如果出现多个任务节点，则并行网关节点默认为结束节点，原因为不考虑多对多情况
        if (targetIds.size() > 1 && currentIds.size() > 1) {
            throw new RuntimeException("任务出现多对多情况，无法驳回");
        }

        // 循环获取那些需要被撤回的节点的ID，用来设置驳回原因
        List<String> currentTaskIds = new ArrayList<>();
        currentIds.forEach(currentId -> runTaskList.forEach(runTask -> {
            if (currentId.equals(runTask.getTaskDefinitionKey())) {
                currentTaskIds.add(runTask.getId());
            }
        }));
        // 设置驳回意见
        currentTaskIds.forEach(item -> {
            if (ObjectUtil.isNotNull(taskBo.getParentTaskId())) {
                taskService.addComment(taskBo.getParentTaskId(), task.getProcessInstanceId(), FlowComment.REJECT.getType(), taskBo.getComment());
            } else {
                taskService.addComment(item, task.getProcessInstanceId(), FlowComment.REJECT.getType(), taskBo.getComment());
            }
        });

        try {
            // 设置处理人
            taskService.setAssignee(task.getId(), TaskUtils.getUserId());
            // 如果父级任务多于 1 个，说明当前节点不是并行节点，原因为不考虑多对多情况
            if (targetIds.size() > 1) {
                // 1 对 多任务跳转，currentIds 当前节点(1)，targetIds 跳转到的节点(多)
                runtimeService.createChangeActivityStateBuilder().processInstanceId(task.getProcessInstanceId()).moveSingleActivityIdToActivityIds(currentIds.get(0), targetIds).changeState();
            }
            // 如果父级任务只有一个，因此当前任务可能为网关中的任务
            if (targetIds.size() == 1) {
                // 1 对 1 或 多 对 1 情况，currentIds 当前要跳转的节点列表(1或多)，targetIds.get(0) 跳转到的节点(1)
                runtimeService.createChangeActivityStateBuilder().processInstanceId(task.getProcessInstanceId()).moveActivityIdsToSingleActivityId(currentIds, targetIds.get(0)).changeState();
            }

            //驳回到目标节点
            List<Task> listTask = taskService.createTaskQuery().processInstanceId(taskBo.getProcInsId()).active().list();
            if (listTask.size() == 1) {
                Task targetTask = listTask.get(0);
                FlowElement targetElement = null;
                if (allElements != null) {
                    for (FlowElement flowElement : allElements) {
                        // 类型为用户节点
                        if (flowElement.getId().equals(targetTask.getTaskDefinitionKey())) {
                            // 获取节点信息
                            targetElement = flowElement;
                        }
                    }
                }

                // 流程发起人
                String startUserId = processInstance.getStartUserId();

                if (targetElement != null) {
                    UserTask targetUserTask = (UserTask) targetElement;

                    if (targetUserTask.getAssignee() != null && StrUtil.equals(targetUserTask.getAssignee().toString(), "${INITIATOR}")) {//是否为发起人节点
                        //开始节点 设置处理人为申请人
                        taskService.setAssignee(targetTask.getId(), startUserId);
                    } else {
                        List<SysUser> sysUserFromTask = getUserFromTask(targetUserTask, startUserId);
                        List<Long> collectUserIdList = sysUserFromTask.stream().filter(Objects::nonNull).map(SysUser::getUserId).filter(Objects::nonNull).collect(Collectors.toList());
                        //collect_username转换成realname
                        List<String> newusername = new ArrayList<String>();
                        for (Long oldUser : collectUserIdList) {
                            SysUser sysUser = userServiceApi.selectUserById(oldUser);
                            newusername.add(sysUser.getNickName());
                        }

                        // 删除后重写
                        for (Long oldUser : collectUserIdList) {
                            taskService.deleteCandidateUser(targetTask.getId(), String.valueOf(oldUser));
                        }

                        for (Long oldUser : collectUserIdList) {
                            taskService.addCandidateUser(targetTask.getId(), String.valueOf(oldUser));
                        }

                        if (collectUserIdList.size() == 1) {
                            targetTask.setAssignee(newusername.get(0).toString());
                            taskService.addUserIdentityLink(targetTask.getId(), collectUserIdList.get(0).toString(), IdentityLinkType.ASSIGNEE);
                        } else if (collectUserIdList.size() > 1) {
                            List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery().activityId(targetTask.getTaskDefinitionKey()).orderByHistoricActivityInstanceStartTime().desc().list();
                            for (HistoricActivityInstance historicActivityInstance : list) {
                                if (StrUtil.isNotBlank(historicActivityInstance.getAssignee())) {
                                    targetTask.setAssignee(historicActivityInstance.getAssignee());
                                    taskService.addUserIdentityLink(targetTask.getId(), historicActivityInstance.getAssignee(), IdentityLinkType.ASSIGNEE);
                                    break;
                                }
                            }
                        }
                    }
                }
            } else if (listTask.size() > 1) {//多任务
                String startUserId = processInstance.getStartUserId();
                String definitionld = runtimeService.createProcessInstanceQuery().processInstanceId(listTask.get(0).getProcessInstanceId()).singleResult().getProcessDefinitionId();   //获取bpm（模型）对象
                BpmnModel bpmnModel = repositoryService.getBpmnModel(definitionld);
                //通过节点定义key获取当前节点
                FlowNode flowNode = (FlowNode) bpmnModel.getFlowElement(listTask.get(0).getTaskDefinitionKey());
                if (Objects.nonNull(flowNode)) {
                    if (flowNode instanceof UserTask) {//当前节点是用户任务
                        UserTask userTask = (UserTask) flowNode;
                        MultiInstanceLoopCharacteristics multiInstance = userTask.getLoopCharacteristics();
                        if (Objects.nonNull(multiInstance) && !multiInstance.isSequential()) {//当前节点是会签而且是并发的话
                            List<SysUser> sysUserFromTask = getUserFromTask(userTask, startUserId);
                            List<Long> userlist = sysUserFromTask.stream().filter(Objects::nonNull).filter(item -> item.getUserId() != null).map(SysUser::getUserId).collect(Collectors.toList());
                            int i = 0;
                            for (Task nexttask : listTask) {
                                String assignee = userlist.get(i).toString();
                                taskService.setAssignee(nexttask.getId(), assignee);
                                i++;
                            }

                        }
                    }
                }
            }

        } catch (FlowableObjectNotFoundException e) {
            throw new RuntimeException("未找到流程实例，流程可能已发生变化");
        } catch (FlowableException e) {
            throw new RuntimeException("无法取消或开始活动");
        }
    }

    public List<SysUser> getUserFromTask(UserTask userTask, String startUserId) {
        String assignee = userTask.getAssignee();
        if (StrUtil.isNotBlank(assignee) && !Objects.equals(assignee, "null") && !Objects.equals(assignee, "${assignee}")) {
            // 指定单人
            if (StringUtils.equalsAnyIgnoreCase(assignee, "${INITIATOR}")) {//对发起人做特殊处理
                SysUser sysUser = new SysUser();
                sysUser.setUserId(Long.valueOf(startUserId));
                return Lists.newArrayList(sysUser);
            } else {
                SysUser userByUsername = userServiceApi.selectUserById(Long.parseLong(assignee));
                return Lists.newArrayList(userByUsername);
            }

        }
        List<String> candidateUsers = userTask.getCandidateUsers();
        if (CollUtil.isNotEmpty(candidateUsers)) {
            // 指定多人
            List<SysUser> list = userServiceApi.getAllUser();
            return list.stream().filter(o -> candidateUsers.contains(String.valueOf(o.getUserId()))).collect(Collectors.toList());
        }
        List<String> candidateGroups = userTask.getCandidateGroups();
        if (CollUtil.isNotEmpty(candidateGroups)) {
            //    指定多组
            List<SysUser> userList = Lists.newArrayList();
            for (String candidateGroup : candidateGroups) {
                List<SysUser> usersByRoleId = userServiceApi.getUserListByRoleId(candidateGroup);
                userList.addAll(usersByRoleId);
            }
            return userList;
        }
        return Lists.newArrayList();
    }

    /**
     * 退回任务
     *
     * @param bo 请求实体参数
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void taskReturn(WfTaskBo bo) {
        // 当前任务 task
        Task task = taskService.createTaskQuery().taskId(bo.getTaskId()).singleResult();
        if (ObjectUtil.isNull(task)) {
            throw new RuntimeException("获取任务信息异常！");
        }
        if (task.isSuspended()) {
            throw new RuntimeException("任务处于挂起状态");
        }
        // 获取流程定义信息
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(task.getProcessDefinitionId()).singleResult();
        // 获取流程模型信息
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
        // 获取当前任务节点元素
        FlowElement source = ModelUtils.getFlowElementById(bpmnModel, task.getTaskDefinitionKey());
        // 获取跳转的节点元素
        FlowElement target = ModelUtils.getFlowElementById(bpmnModel, bo.getTargetKey());
        // 从当前节点向前扫描，判断当前节点与目标节点是否属于串行，若目标节点是在并行网关上或非同一路线上，不可跳转
        boolean isSequential = ModelUtils.isSequentialReachable(source, target, new HashSet<>());
        if (!isSequential) {
            throw new RuntimeException("当前节点相对于目标节点，不属于串行关系，无法回退");
        }

        // 获取所有正常进行的任务节点 Key，这些任务不能直接使用，需要找出其中需要撤回的任务
        List<Task> runTaskList = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).list();
        List<String> runTaskKeyList = new ArrayList<>();
        runTaskList.forEach(item -> runTaskKeyList.add(item.getTaskDefinitionKey()));
        // 需退回任务列表
        List<String> currentIds = new ArrayList<>();
        // 通过父级网关的出口连线，结合 runTaskList 比对，获取需要撤回的任务
        List<UserTask> currentUserTaskList = FlowableUtils.iteratorFindChildUserTasks(target, runTaskKeyList, null, null);
        currentUserTaskList.forEach(item -> currentIds.add(item.getId()));
        // 循环获取那些需要被撤回的节点的ID，用来设置驳回原因
        List<String> currentTaskIds = new ArrayList<>();
        currentIds.forEach(currentId -> runTaskList.forEach(runTask -> {
            if (currentId.equals(runTask.getTaskDefinitionKey())) {
                currentTaskIds.add(runTask.getId());
            }
        }));
        identityService.setAuthenticatedUserId(TaskUtils.getUserId());
        // 设置回退意见
        for (String currentTaskId : currentTaskIds) {
            if (ObjectUtil.isNotNull(bo.getParentTaskId())) {
                taskService.addComment(bo.getParentTaskId(), task.getProcessInstanceId(), FlowComment.REBACK.getType(), bo.getComment());
            } else {
                taskService.addComment(currentTaskId, task.getProcessInstanceId(), FlowComment.REBACK.getType(), bo.getComment());
            }
        }
        try {
            // 1 对 1 或 多 对 1 情况，currentIds 当前要跳转的节点列表(1或多)，targetKey 跳转到的节点(1)
            runtimeService.createChangeActivityStateBuilder().processInstanceId(task.getProcessInstanceId()).moveActivityIdsToSingleActivityId(currentIds, bo.getTargetKey()).changeState();
        } catch (FlowableObjectNotFoundException e) {
            throw new RuntimeException("未找到流程实例，流程可能已发生变化");
        } catch (FlowableException e) {
            throw new RuntimeException("无法取消或开始活动");
        }
        // 设置任务节点名称
        bo.setTaskName(task.getName());
        // 处理抄送用户
        if (!copyService.makeCopy(bo)) {
            throw new RuntimeException("抄送任务失败");
        }
    }


    /**
     * 获取所有可回退的节点
     *
     * @param bo
     * @return
     */
    @Override
    public List<FlowElement> findReturnTaskList(WfTaskBo bo) {
        // 当前任务 task
        Task task = taskService.createTaskQuery().taskId(bo.getTaskId()).singleResult();
        // 获取流程定义信息
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(task.getProcessDefinitionId()).singleResult();
        // 获取流程模型信息
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
        // 查询历史节点实例
        List<HistoricActivityInstance> activityInstanceList = historyService.createHistoricActivityInstanceQuery().processInstanceId(task.getProcessInstanceId()).activityType(BpmnXMLConstants.ELEMENT_TASK_USER).finished().orderByHistoricActivityInstanceEndTime().asc().list();
        List<String> activityIdList = activityInstanceList.stream().map(HistoricActivityInstance::getActivityId).filter(activityId -> !StringUtils.equals(activityId, task.getTaskDefinitionKey())).distinct().collect(Collectors.toList());
        // 获取当前任务节点元素
        FlowElement source = ModelUtils.getFlowElementById(bpmnModel, task.getTaskDefinitionKey());
        List<FlowElement> elementList = new ArrayList<>();
        for (String activityId : activityIdList) {
            FlowElement target = ModelUtils.getFlowElementById(bpmnModel, activityId);
            boolean isSequential = ModelUtils.isSequentialReachable(source, target, new HashSet<>());
            if (isSequential) {
                elementList.add(target);
            }
        }
        return elementList;
    }

    /**
     * 删除任务
     *
     * @param bo 请求实体参数
     */
    @Override
    public void deleteTask(WfTaskBo bo) {
        // todo 待确认删除任务是物理删除任务 还是逻辑删除，让这个任务直接通过？
        identityService.setAuthenticatedUserId(TaskUtils.getUserId());
        taskService.deleteTask(bo.getTaskId(), bo.getComment());
    }

    /**
     * 认领/签收任务
     *
     * @param taskBo 请求实体参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void claim(WfTaskBo taskBo) {
        Task task = taskService.createTaskQuery().taskId(taskBo.getTaskId()).singleResult();
        if (Objects.isNull(task)) {
            throw new ServiceException("任务不存在");
        }
        taskService.claim(taskBo.getTaskId(), TaskUtils.getUserId());
    }

    /**
     * 取消认领/签收任务
     *
     * @param bo 请求实体参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unClaim(WfTaskBo bo) {
        taskService.unclaim(bo.getTaskId());
    }

    /**
     * 委派任务
     *
     * @param bo 请求实体参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delegateTask(WfTaskBo bo) {
        // 当前任务 task
        Task task = taskService.createTaskQuery().taskId(bo.getTaskId()).singleResult();
        if (ObjectUtil.isEmpty(task)) {
            throw new ServiceException("获取任务失败！");
        }
        StringBuilder commentBuilder = new StringBuilder(SecurityUtils.getLoginUser().getUser().getNickName()).append("->");
        SysUser sysUser = userServiceApi.selectUserById(Long.parseLong(bo.getUserId()));
        String nickName = sysUser.getNickName();
        if (StringUtils.isNotBlank(nickName)) {
            commentBuilder.append(nickName);
        } else {
            commentBuilder.append(bo.getUserId());
        }
        if (StringUtils.isNotBlank(bo.getComment())) {
            commentBuilder.append(": ").append(bo.getComment());
        }
        identityService.setAuthenticatedUserId(TaskUtils.getUserId());
        // 添加审批意见
        if (ObjectUtil.isNotNull(bo.getParentTaskId())) {
            taskService.addComment(bo.getParentTaskId(), task.getProcessInstanceId(), FlowComment.DELEGATE.getType(), commentBuilder.toString());
        } else {
            taskService.addComment(bo.getTaskId(), task.getProcessInstanceId(), FlowComment.DELEGATE.getType(), commentBuilder.toString());
        }
        // 设置办理人为当前登录人
        taskService.setOwner(bo.getTaskId(), TaskUtils.getUserId());
        // 执行委派
        taskService.delegateTask(bo.getTaskId(), bo.getUserId());
        // 设置任务节点名称
        bo.setTaskName(task.getName());
        // 处理抄送用户
        if (!copyService.makeCopy(bo)) {
            throw new RuntimeException("抄送任务失败");
        }
    }


    /**
     * 转办任务
     *
     * @param bo 请求实体参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void transferTask(WfTaskBo bo) {
        // 当前任务 task
        Task task = taskService.createTaskQuery().taskId(bo.getTaskId()).singleResult();
        if (ObjectUtil.isEmpty(task)) {
            throw new ServiceException("获取任务失败！");
        }
        StringBuilder commentBuilder = new StringBuilder(SecurityUtils.getLoginUser().getUser().getNickName()).append("->");
        SysUser sysUser = userServiceApi.selectUserById(Long.parseLong(bo.getUserId()));
        String nickName = sysUser.getNickName();
        if (StringUtils.isNotBlank(nickName)) {
            commentBuilder.append(nickName);
        } else {
            commentBuilder.append(bo.getUserId());
        }
        if (StringUtils.isNotBlank(bo.getComment())) {
            commentBuilder.append(": ").append(bo.getComment());
        }
        identityService.setAuthenticatedUserId(TaskUtils.getUserId());
        // 添加审批意见
        if (ObjectUtil.isNotNull(bo.getParentTaskId())) {
            taskService.addComment(bo.getParentTaskId(), task.getProcessInstanceId(), FlowComment.TRANSFER.getType(), commentBuilder.toString());
        } else {
            taskService.addComment(bo.getTaskId(), task.getProcessInstanceId(), FlowComment.TRANSFER.getType(), commentBuilder.toString());
        }
        // 设置拥有者为当前登录人
        taskService.setOwner(bo.getTaskId(), TaskUtils.getUserId());
        // 转办任务
        taskService.setAssignee(bo.getTaskId(), bo.getUserId());
        // 设置任务节点名称
        bo.setTaskName(task.getName());
        // 处理抄送用户
        if (!copyService.makeCopy(bo)) {
            throw new RuntimeException("抄送任务失败");
        }
    }

    /**
     * 取消申请
     *
     * @param bo
     * @return
     */
    @Override
    public void stopProcess(WfTaskBo bo) {
        List<Task> task = taskService.createTaskQuery().processInstanceId(bo.getProcInsId()).list();
        if (CollectionUtils.isEmpty(task)) {
            throw new RuntimeException("流程未启动或已执行完成，取消申请失败");
        }

        // 创建流程实例
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(bo.getProcInsId()).singleResult();
        // 获取流程模型
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
        if (Objects.nonNull(bpmnModel)) {
            // 获取主流程
            Process process = bpmnModel.getMainProcess();
            // 获取结束节点
            List<EndEvent> endNodes = process.findFlowElementsOfType(EndEvent.class, false);
            if (CollectionUtils.isNotEmpty(endNodes)) {
                // 设置当前用户为认证用户
                Authentication.setAuthenticatedUserId(TaskUtils.getUserId());
                // 添加流程评论
                taskService.addComment(task.get(0).getId(), processInstance.getProcessInstanceId(), FlowComment.STOP.getType(), StringUtils.isBlank(bo.getComment()) ? "取消申请" : bo.getComment());
                // 获取最后一个结束节点
                String endId = endNodes.get(0).getId();
                // 获取当前流程执行
                List<Execution> executions = runtimeService.createExecutionQuery().parentId(processInstance.getProcessInstanceId()).list();
                // 存储执行ID
                List<String> executionIds = new ArrayList<>();
                executions.forEach(execution -> executionIds.add(execution.getId()));
                runtimeService.setVariable(processInstance.getProcessInstanceId(), ProcessConstants.PROCESS_STATUS_KEY, ProcessStatus.CANCELED.getStatus());
                // 变更流程为已结束状态
                runtimeService.createChangeActivityStateBuilder().moveExecutionsToSingleActivityId(executionIds, endId).changeState();
            }
        }
    }

    /**
     * 撤回流程
     *
     * @param taskBo 请求实体参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void revokeProcess(WfTaskBo taskBo) {
        String procInsId = taskBo.getProcInsId();
        String taskId = taskBo.getTaskId();
        // 校验流程是否结束
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(procInsId).active().singleResult();
        if (ObjectUtil.isNull(processInstance)) {
            throw new RuntimeException("流程已结束或已挂起，无法执行撤回操作");
        }
        // 获取待撤回任务实例
        HistoricTaskInstance currTaskIns = historyService.createHistoricTaskInstanceQuery().taskId(taskId).taskAssignee(TaskUtils.getUserId()).singleResult();
        if (ObjectUtil.isNull(currTaskIns)) {
            throw new RuntimeException("当前任务不存在，无法执行撤回操作");
        }
        // 获取 bpmn 模型
        BpmnModel bpmnModel = repositoryService.getBpmnModel(currTaskIns.getProcessDefinitionId());
        UserTask currUserTask = ModelUtils.getUserTaskByKey(bpmnModel, currTaskIns.getTaskDefinitionKey());
        // 查找下一级用户任务列表
        List<UserTask> nextUserTaskList = ModelUtils.findNextUserTasks(currUserTask);
        List<String> nextUserTaskKeys = nextUserTaskList.stream().map(UserTask::getId).collect(Collectors.toList());

        // 获取当前节点之后已完成的流程历史节点
        List<HistoricTaskInstance> finishedTaskInsList = historyService.createHistoricTaskInstanceQuery().processInstanceId(procInsId).taskCreatedAfter(currTaskIns.getEndTime()).finished().list();
        for (HistoricTaskInstance finishedTaskInstance : finishedTaskInsList) {
            // 检查已完成流程历史节点是否存在下一级中
            if (CollUtil.contains(nextUserTaskKeys, finishedTaskInstance.getTaskDefinitionKey())) {
                throw new RuntimeException("下一流程已处理，无法执行撤回操作");
            }
        }
        // 获取所有激活的任务节点，找到需要撤回的任务
        List<Task> activateTaskList = taskService.createTaskQuery().processInstanceId(procInsId).list();
        List<String> revokeExecutionIds = new ArrayList<>();
        identityService.setAuthenticatedUserId(TaskUtils.getUserId());
        for (Task task : activateTaskList) {
            // 检查激活的任务节点是否存在下一级中，如果存在，则加入到需要撤回的节点
            if (CollUtil.contains(nextUserTaskKeys, task.getTaskDefinitionKey())) {
                // 添加撤回审批信息
                taskService.setAssignee(task.getId(), TaskUtils.getUserId());
                taskService.addComment(task.getId(), task.getProcessInstanceId(), FlowComment.REVOKE.getType(), SecurityUtils.getLoginUser().getUser().getNickName() + "撤回流程审批");
                revokeExecutionIds.add(task.getExecutionId());
            }
        }
        try {
            ChangeActivityStateBuilder changeActivityStateBuilder = runtimeService.createChangeActivityStateBuilder().processInstanceId(procInsId).moveExecutionsToSingleActivityId(revokeExecutionIds, currTaskIns.getTaskDefinitionKey());
            changeActivityStateBuilder.changeState();
        } catch (FlowableObjectNotFoundException e) {
            throw new RuntimeException("未找到流程实例，流程可能已发生变化");
        } catch (FlowableException e) {
            throw new RuntimeException("执行撤回操作失败");
        } catch (Exception e) {
            throw new RuntimeException("已经撤回");
        }
    }

    /**
     * 获取流程过程图
     *
     * @param processId
     * @return
     */
    @Override
    public InputStream diagram(String processId) {
        String processDefinitionId;
        // 获取当前的流程实例
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processId).singleResult();
        // 如果流程已经结束，则得到结束节点
        if (Objects.isNull(processInstance)) {
            HistoricProcessInstance pi = historyService.createHistoricProcessInstanceQuery().processInstanceId(processId).singleResult();

            processDefinitionId = pi.getProcessDefinitionId();
        } else {// 如果流程没有结束，则取当前活动节点
            // 根据流程实例ID获得当前处于活动状态的ActivityId合集
            ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processId).singleResult();
            processDefinitionId = pi.getProcessDefinitionId();
        }

        // 获得活动的节点
        List<HistoricActivityInstance> highLightedFlowList = historyService.createHistoricActivityInstanceQuery().processInstanceId(processId).orderByHistoricActivityInstanceStartTime().asc().list();

        List<String> highLightedFlows = new ArrayList<>();
        List<String> highLightedNodes = new ArrayList<>();
        //高亮线
        for (HistoricActivityInstance tempActivity : highLightedFlowList) {
            if ("sequenceFlow".equals(tempActivity.getActivityType())) {
                //高亮线
                highLightedFlows.add(tempActivity.getActivityId());
            } else {
                //高亮节点
                highLightedNodes.add(tempActivity.getActivityId());
            }
        }
        //获取流程图
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        ProcessEngineConfiguration configuration = processEngine.getProcessEngineConfiguration();
        //获取自定义图片生成器
        ProcessDiagramGenerator diagramGenerator = new CustomProcessDiagramGenerator();
        return diagramGenerator.generateDiagram(bpmnModel, "png", highLightedNodes, highLightedFlows, configuration.getActivityFontName(), configuration.getLabelFontName(), configuration.getAnnotationFontName(), configuration.getClassLoader(), 1.0, true);

    }

    /**
     * 获取流程变量
     *
     * @param taskId 任务ID
     * @return 流程变量
     */
    @Override
    public Map<String, Object> getProcessVariables(String taskId) {
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().includeProcessVariables().finished().taskId(taskId).singleResult();
        if (Objects.nonNull(historicTaskInstance)) {
            return historicTaskInstance.getProcessVariables();
        }
        return taskService.getVariables(taskId);
    }

    /**
     * 启动第一个任务
     *
     * @param processInstance 流程实例
     * @param variables       流程参数
     */
    @Override
    public void startFirstTask(ProcessInstance processInstance, Map<String, Object> variables) {
        // 若第一个用户任务为发起人，则自动完成任务
        // 获取指定流程实例ID的任务列表
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstance.getProcessInstanceId()).list();
        // 如果任务列表不为空
        if (CollUtil.isNotEmpty(tasks)) {
            // 获取发起人ID
            String userIdStr = (String) variables.get(TaskConstants.PROCESS_INITIATOR);
            // 设置当前登录用户
            identityService.setAuthenticatedUserId(TaskUtils.getUserId());
            // 遍历任务列表
            for (Task task : tasks) {
                // 如果任务assignee为发起人ID
                if (StrUtil.equals(task.getAssignee(), userIdStr)) {
                    // 添加流程评论
                    taskService.addComment(task.getId(), processInstance.getProcessInstanceId(), FlowComment.NORMAL.getType(), SecurityUtils.getLoginUser().getUser().getNickName() + "发起流程申请");
                    // 完成任务
                    taskService.complete(task.getId(), variables);
                }
            }
        }
    }

    /**
     * 加签任务
     *
     * @param bo
     */
    @Override
    public void addSignTask(WfTaskBo bo) {
        //登录用户
        String userName = String.valueOf(SecurityUtils.getUserId());
        String nickName = SecurityUtils.getLoginUser().getUser().getNickName();
        String[] userIds = bo.getAddSignUsers().split(",");
        List<SysUser> sysUserList = userServiceApi.selectSysUserByUserIdList(Arrays.stream(userIds).mapToLong(Long::parseLong).toArray());
        String nick = StrUtil.join(",", sysUserList.stream().map(SysUser::getNickName).collect(Collectors.toList()));
        TaskEntityImpl taskEntity = (TaskEntityImpl) taskService.createTaskQuery().taskId(bo.getTaskId()).singleResult();
        if (taskEntity != null) {
            if (StringUtils.equalsIgnoreCase(bo.getAddSignType(), "0")) {
                addTasksBefore(bo, taskEntity, userName, new HashSet<String>(Arrays.asList(userIds)), nickName + "在[" + taskEntity.getName() + "]节点前加签,加签人员【" + nick + "】原因:" + bo.getComment());
            } else {
                addTasksAfter(bo, taskEntity, userName, new HashSet<String>(Arrays.asList(userIds)), nickName + "在[" + taskEntity.getName() + "]节点后加签,加签人员【" + nick + "】原因:" + bo.getComment());
            }
        } else {
            Assert.notNull("不存在任务实例，请确认!");
        }
    }

    /**
     * 多实例加签任务
     *
     * @param bo
     */
    @Override
    public void multiInstanceAddSign(WfTaskBo bo) {
        //校验任务是否存在
        Task task = taskService.createTaskQuery().taskId(bo.getTaskId()).singleResult();
        //流程定义id
        String processDefinitionId = task.getProcessDefinitionId();
        //流程实例id
        String processInstanceId = task.getProcessInstanceId();
        //当前活动节点id
        String currentActId = task.getTaskDefinitionKey();
        //当前活动节点名称（任务名称）
        String currentActName = task.getName();
        //多实例用户任务节点的元素变量名
        String multiInstanceActAssigneeParam = getMultiInstanceActAssigneeParam(processDefinitionId, currentActId);
        //如果元素变量名为空则表示该节点不是会签节点
        if (ObjectUtil.isEmpty(multiInstanceActAssigneeParam)) {
            throw new FlowableException("加签失败，该任务不是会签（或签）任务或节点配置错误");
        }
        //加签人的姓名
        List<String> assigneeNameList = CollectionUtil.newArrayList();
        String[] userIds = bo.getAddSignUsers().split(",");
        List<String> assigneeList = new ArrayList<String>();
        assigneeList = Arrays.asList(userIds);
        //遍历要加签的人
        assigneeList.forEach(assignee -> {
            //获取加签人名称
            String assigneeName = userServiceApi.selectUserById(Long.valueOf(assignee)).getNickName();
            assigneeNameList.add(assigneeName);
            //定义参数
            Map<String, Object> assigneeVariables = new HashMap<String, Object>(16);
            //根据获取的变量名加参数
            assigneeVariables.put(multiInstanceActAssigneeParam, assignee);
            //执行加签操作
            try {
                runtimeService.addMultiInstanceExecution(currentActId, processInstanceId, assigneeVariables);
            } catch (FlowableException e) {
                //抛异常加签失败
                throw new FlowableException("加签失败，该任务不是会签（或签）任务或节点配置错误");
            } catch (Exception e) {
                //否则的话，可能出现服务器内部异常
                throw new FlowableException("服务器出现异常，请联系管理员");
            }
        });
        //当前办理人姓名
        String name = SecurityUtils.getLoginUser().getUser().getNickName();
        //添加加签意见
        String type = FlowComment.DSLJQ.getType();

        if (ObjectUtil.isNotNull(bo.getParentTaskId())) {
            List<SysUser> sysUserList = userServiceApi.selectSysUserByUserIdList(Arrays.stream(userIds).mapToLong(Long::parseLong).toArray());
            String nick = StrUtil.join(",", sysUserList.stream().map(SysUser::getNickName).collect(Collectors.toList()));
            taskService.addComment(task.getParentTaskId(), processInstanceId, type, name + "加签原因加签人员【" + nick + "】:" + bo.getComment());
        } else {
            taskService.addComment(task.getId(), processInstanceId, type, name + "加签原因:" + bo.getComment());
        }
    }

    /**
     * 收回流程,收回后发起人可以重新编辑表单发起流程，对于自定义业务就是原有任务都删除，重新进行申请
     *
     * @param bo
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R recallProcess(WfTaskBo bo) {
        // 当前任务 listtask
        List<Task> listtask = taskService.createTaskQuery().processInstanceId(bo.getProcInsId()).active().list();
        if (listtask == null || listtask.size() == 0) {
            throw new FlowableException("流程未启动或已执行完成，无法收回");
        }

        if (taskService.createTaskQuery().taskId(listtask.get(0).getId()).singleResult().isSuspended()) {
            throw new FlowableException("任务处于挂起状态");
        }

        String processInstanceId = listtask.get(0).getProcessInstanceId();

        //  获取所有历史任务（按创建时间升序）
        List<HistoricTaskInstance> hisTaskList = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).orderByTaskCreateTime().asc().list();
        if (CollectionUtil.isEmpty(hisTaskList) || hisTaskList.size() < 2) {
            log.error("当前流程 【{}】 审批节点 【{}】正在初始节点无法收回", processInstanceId, listtask.get(0).getName());
            throw new FlowableException(String.format("当前流程 【%s】 审批节点【%s】正在初始节点无法收回", processInstanceId, listtask.get(0).getName()));
        }

        //  第一个任务
        HistoricTaskInstance startTask = hisTaskList.get(0);
        //若操作用户不是发起人，不能收回
        if (!StringUtils.equalsAnyIgnoreCase(String.valueOf(SecurityUtils.getUserId()), startTask.getAssignee())) {
            throw new FlowableException("操作用户不是发起人，不能收回");
        }
        //  当前任务
        HistoricTaskInstance currentTask = hisTaskList.get(hisTaskList.size() - 1);

        BpmnModel bpmnModel = repositoryService.getBpmnModel(listtask.get(0).getProcessDefinitionId());

        //  获取第一个活动节点
        FlowNode startFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(startTask.getTaskDefinitionKey());
        //  获取当前活动节点
        FlowNode currentFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(currentTask.getTaskDefinitionKey());

        UserTask startUserTask = (UserTask) startFlowNode;
        UserTask currentUserTask = (UserTask) currentFlowNode;
        if (startUserTask.getId().equals(currentUserTask.getId())) {
            throw new RuntimeException("当前节点是已是最初节点，无法收回");
        }

        //  临时保存当前活动的原始方向
        List<SequenceFlow> originalSequenceFlowList = new ArrayList<>(currentFlowNode.getOutgoingFlows());
        //  清理活动方向
        currentFlowNode.getOutgoingFlows().clear();

        //  建立新方向
        SequenceFlow newSequenceFlow = new SequenceFlow();
        newSequenceFlow.setId("newSequenceFlowId");
        newSequenceFlow.setSourceFlowElement(currentFlowNode);
        newSequenceFlow.setTargetFlowElement(startFlowNode);
        List<SequenceFlow> newSequenceFlowList = new ArrayList<>();
        newSequenceFlowList.add(newSequenceFlow);
        //  当前节点指向新的方向
        currentFlowNode.setOutgoingFlows(newSequenceFlowList);

        //  完成当前任务
        for (Task task : listtask) {
            taskService.addComment(task.getId(), listtask.get(0).getProcessInstanceId(), FlowComment.RECALL.getType(), "发起人收回");
            taskService.setAssignee(task.getId(), startTask.getAssignee());
            taskService.complete(task.getId());
        }


        //  重新查询当前任务
        Task nextTask = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
        if (ObjectUtil.isNotNull(nextTask)) {
            taskService.setAssignee(nextTask.getId(), startTask.getAssignee());
//            taskService.complete(nextTask.getId());;//跳过流程发起节点
        }
        //  恢复原始方向
        currentFlowNode.setOutgoingFlows(originalSequenceFlowList);
        return R.ok("发起人收回成功");
    }

    /**
     * 拒绝任务
     *
     * @param taskBo
     */
    @Override
    public void taskRefuse(WfTaskBo taskBo) {
        // 当前任务 task
        Task task = taskService.createTaskQuery().taskId(taskBo.getTaskId()).singleResult();
        if (ObjectUtil.isNull(task)) {
            throw new RuntimeException("获取任务信息异常！");
        }
        if (task.isSuspended()) {
            throw new RuntimeException("任务处于挂起状态");
        }
        // 获取流程实例
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(taskBo.getProcInsId()).singleResult();
        if (processInstance == null) {
            throw new RuntimeException("流程实例不存在，请确认！");
        }
        // 获取流程定义信息
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(task.getProcessDefinitionId()).singleResult();

        // 添加审批意见
        if (ObjectUtil.isNotNull(taskBo.getParentTaskId()) && !StrUtil.isEmpty(taskBo.getParentTaskId()))  {
            taskService.addComment(taskBo.getParentTaskId(), taskBo.getProcInsId(), FlowComment.REFUSE.getType(), taskBo.getComment());
        } else {
            taskService.addComment(taskBo.getTaskId(), taskBo.getProcInsId(), FlowComment.REFUSE.getType(), taskBo.getComment());
        }

        // 设置流程状态为已终结
        runtimeService.setVariable(processInstance.getId(), ProcessConstants.PROCESS_STATUS_KEY, ProcessStatus.TERMINATED.getStatus());
        // 获取所有节点信息
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
        EndEvent endEvent = ModelUtils.getEndEvent(bpmnModel);
        // 终止流程
        List<Execution> executions = runtimeService.createExecutionQuery().parentId(task.getProcessInstanceId()).list();
        List<String> executionIds = executions.stream().map(Execution::getId).collect(Collectors.toList());
        runtimeService.createChangeActivityStateBuilder().processInstanceId(task.getProcessInstanceId()).moveExecutionsToSingleActivityId(executionIds, endEvent.getId()).changeState();
        // 处理抄送用户
        if (!copyService.makeCopy(taskBo)) {
            throw new RuntimeException("抄送任务失败");
        }
    }

    /**
     * 跳转任务
     *
     * @param bo
     */
    @Override
    public void taskJump(WfTaskBo bo) {
        //校验任务是否存在
        Task task = taskService.createTaskQuery().taskId(bo.getTaskId()).singleResult();
        //当前节点id
        String currentActId = task.getTaskDefinitionKey();
        //获取流程实例id
        String processInstanceId = task.getProcessInstanceId();
        //当前活动节点名称（任务名称）
        String currentActName = task.getName();
        //获取当前操作人姓名
        String name = SecurityUtils.getLoginUser().getUser().getNickName();
        String type = FlowComment.JUMP.getType();
        //添加跳转意见 name + "将任务跳转到【" + targetActName + "】，跳转原因：" + comment + ";";
        if (ObjectUtil.isNotNull(bo.getParentTaskId())) {
            taskService.addComment(task.getParentTaskId(), processInstanceId, type, "当前任务[" + currentActName + "]由" + name + "跳转到[" + bo.getTargetActName() + "]，跳转原因：" + bo.getComment());
        } else {
            taskService.addComment(task.getId(), processInstanceId, type, "当前任务[" + currentActName + "]由" + name + "跳转到[" + bo.getTargetActName() + "]，跳转原因：" + bo.getComment());
        }
        // 获取流程定义信息
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(task.getProcessDefinitionId()).singleResult();
        // 获取流程模型信息
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
        // 获取当前任务节点元素
        FlowElement source = ModelUtils.getFlowElementById(bpmnModel, task.getTaskDefinitionKey());
        // 获取跳转的节点元素
        FlowElement target = ModelUtils.getFlowElementById(bpmnModel, bo.getTargetActId());
        // 从当前节点向前扫描，判断当前节点与目标节点是否属于串行，若目标节点是在并行网关上或非同一路线上，不可跳转
        boolean isSequential = ModelUtils.isSequentialReachable(source, target, new HashSet<>());
        if (!isSequential) {
            throw new RuntimeException("当前节点相对于目标节点，不属于串行关系，无法回退");
        }
        //执行跳转操作
        runtimeService.createChangeActivityStateBuilder().processInstanceId(processInstanceId).moveActivityIdTo(currentActId, bo.getTargetActId()).changeState();
    }

    /**
     * 用户任务列表,作为跳转任务使用
     *
     * @param bo
     * @return
     */
    @Override
    public R userTaskList(WfTaskBo bo) {
        List<WfUserTaskVo> resultList = new ArrayList<WfUserTaskVo>();

        // 当前任务 task
        Task task = taskService.createTaskQuery().taskId(bo.getTaskId()).singleResult();
        // 获取流程定义信息
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(task.getProcessDefinitionId()).singleResult();

        //根据流程定义获取deployment
        String deploymentId = processDefinition.getDeploymentId();
        Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(deploymentId).singleResult();
        if (ObjectUtil.isEmpty(deployment)) {
            throw new FlowableException("流程还没布置");
        }

        //获取bpmnModel并转为modelNode
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
        //获取主流程
        Process mainProcess = bpmnModel.getMainProcess();
        //获取用户任务节点类型，深入子流程
        mainProcess.findFlowElementsOfType(UserTask.class, true).forEach(userTask -> {
            // 判断不是发起人
            if (!"${initiator}".equals(userTask.getAssignee())) {
                WfUserTaskVo userTaskResult = new WfUserTaskVo();
                userTaskResult.setId(userTask.getId());
                userTaskResult.setProcessDefinitionId(processDefinition.getId());
                userTaskResult.setName(userTask.getName());
                resultList.add(userTaskResult);
            }
        });
        return R.ok(resultList);
    }

    /**
     * 监听任务创建事件
     *
     * @param task 任务实体
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void updateTaskStatusWhenCreated(Task task) {
        // 获取流程发起人
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().includeProcessVariables().processInstanceId(task.getProcessInstanceId()).singleResult();
        Long startUserId = Long.valueOf(processInstance.getStartUserId());
//        SysUser startUser = remoteUserService.selectSysUserByUserId(startUserId, SecurityConstants.INNER).getData();

        // 获取指定任务审批人
        List<IdentityLink> identityLinksForTask = taskService.getIdentityLinksForTask(task.getId());
        // 遍历 identityLinksForTask 中的 IdentityLink 对象
//        for (IdentityLink identityLink : identityLinksForTask) {
//            // 如果 IdentityLink 的类型是 Candidate
//            if (IdentityLinkType.CANDIDATE.equals(identityLink.getType())) {
////                 获取审批人的用户ID
//                Long userId = Long.valueOf(identityLink.getUserId());
//                SysUser user = remoteUserService.selectSysUserByUserId(userId, SecurityConstants.INNER).getData();
//                SysMessageVo sysMessageVo = new SysMessageVo();
//                sysMessageVo.setUserId(startUserId);
//                sysMessageVo.setUserName(startUser.getNickName());
//                sysMessageVo.setTitle("【" + startUser.getNickName() + "】流程待办");
//                sysMessageVo.setCategory(MessageCategoryConstant.MESSAGE_CATEGORY_1);
//                sysMessageVo.setRange(MessageRangeConstant.MESSAGE_RANGE_1);
//                sysMessageVo.setSendingTime(DateUtils.getNowDate());
//                sysMessageVo.setContent(startUser.getNickName() + "发起" + "【" + processInstance.getProcessDefinitionName() + "】流程待办，请及时处理");
//                sysMessageVo.setType(MessageTypeConstant.MESSAGE_TYPE_TEXT);
//                sysMessageVo.setReceiveUserId(userId);
//                List<SysMessageReceive> sysMessageReceiveList = new ArrayList<>();
//                SysMessageReceive sysMessageReceive = new SysMessageReceive();
//                sysMessageReceive.setReceiveId(userId);
//                sysMessageReceive.setReceive(user.getNickName());
//                sysMessageReceiveList.add(sysMessageReceive);
//                sysMessageVo.setSysMessageReceiveList(sysMessageReceiveList);
//                remoteSysMessageService.addSysMessage(sysMessageVo, SecurityConstants.INNER);
//            }
//        }
    }

    private MessageSendWhenTaskCreatedReq convert(ProcessInstance processInstance, SysUser user, Task task) {
        MessageSendWhenTaskCreatedReq reqDTO = new MessageSendWhenTaskCreatedReq();
        reqDTO.setProcessInstanceId(processInstance.getProcessInstanceId());
        reqDTO.setProcessInstanceName(processInstance.getName());
        reqDTO.setStartUserId(user.getUserId());
        reqDTO.setStartUserNickname(user.getNickName());
        reqDTO.setTaskId(task.getId());
        reqDTO.setTaskName(task.getName());
        reqDTO.setAssigneeUserId(NumberUtils.parseLong(task.getAssignee()));
        return reqDTO;
    }

    /**
     * 任务前加签 （如果多次加签只能显示第一次前加签的处理人来处理任务）
     * 多个加签人处理完毕任务之后又流到自己这里
     *
     * @param bo          流程实例id
     * @param assignee    受让人
     * @param description 描述
     * @param assignees   被加签人
     */
    private void addTasksBefore(WfTaskBo bo, TaskEntityImpl taskEntity, String assignee, Set<String> assignees, String description) {
        addTask(bo, taskEntity, assignee, assignees, description, Boolean.FALSE);
    }

    /**
     * 任务后加签（加签人自己自动审批完毕加签多个人处理任务）
     *
     * @param bo          流程实例id
     * @param assignee    受让人
     * @param description 描述
     * @param assignees   被加签人
     */
    private void addTasksAfter(WfTaskBo bo, TaskEntityImpl taskEntity, String assignee, Set<String> assignees, String description) {
        addTask(bo, taskEntity, assignee, assignees, description, Boolean.TRUE);
    }


    /**
     * 创建加签任务
     *
     * @param bo
     * @param taskEntity
     * @param assignee
     * @param assignees
     * @param description
     * @param flag
     */
    @Transactional(rollbackFor = Exception.class)
    public void addTask(WfTaskBo bo, TaskEntityImpl taskEntity, String assignee, Set<String> assignees, String description, Boolean flag) {
        Assert.notNull(taskEntity, String.format("分配人 [%s] 没有待处理任务", assignee));

        //如果是加签再加签
        String parentTaskId = taskEntity.getParentTaskId();
        if (StrUtil.isBlank(parentTaskId)) {
            taskEntity.setOwner(assignee);
            taskEntity.setAssignee(null);
            taskEntity.setCountEnabled(true);
            if (flag) {
                taskEntity.setScopeType("after");
            } else {
                taskEntity.setScopeType("before");
            }
            // 设置任务为空执行者
            taskService.saveTask(taskEntity);
        }
        //添加加签数据
        this.createSignSubTasks(assignee, assignees, taskEntity);
        //添加审批意见
        String type = flag ? FlowComment.HJQ.getType() : FlowComment.QJQ.getType();
        taskService.addComment(taskEntity.getId(), bo.getProcInsId(), type, description);
    }

    /**
     * 创建加签子任务
     *
     * @param assignees  被加签人
     * @param assignee   加签人
     * @param taskEntity 父任务
     */
    private void createSignSubTasks(String assignee, Set<String> assignees, TaskEntity taskEntity) {
        if (CollectionUtil.isNotEmpty(assignees)) {
            //1.创建被加签人的任务列表
            assignees.forEach(userId -> {
                if (StrUtil.isNotBlank(userId)) {
                    this.createSubTask(taskEntity, taskEntity.getId(), userId);
                }
            });

            String parentTaskId = taskEntity.getParentTaskId();
            if (StrUtil.isBlank(parentTaskId)) {
                parentTaskId = taskEntity.getId();
            }
            String finalParentTaskId = parentTaskId;
            //2.创建加签人的任务并执行完毕
            String taskId = taskEntity.getId();
            if (StrUtil.isBlank(taskEntity.getParentTaskId())) {
                Task task = this.createSubTask(taskEntity, finalParentTaskId, assignee);
                taskId = task.getId();
            }
            Task taskInfo = taskService.createTaskQuery().taskId(taskId).singleResult();
            if (ObjectUtil.isNotNull(taskInfo)) {
                taskService.complete(taskId);
            }
            //如果是候选人，需要删除运行时候选不中的数据。
            long candidateCount = taskService.createTaskQuery().taskId(parentTaskId).taskCandidateUser(assignee).count();
            if (candidateCount > 0) {
                taskService.deleteCandidateUser(parentTaskId, assignee);
            }
        }
    }

    /**
     * 创建子任务
     *
     * @param ptask    创建子任务
     * @param assignee 子任务的执行人
     * @return
     */
    TaskEntity createSubTask(TaskEntity ptask, String ptaskId, String assignee) {
        TaskEntity task = null;
        if (ptask != null) {
            //1.生成子任务
            task = (TaskEntity) taskService.newTask(UUID.randomUUID() + "");
            task.setCategory(ptask.getCategory());
            task.setDescription(ptask.getDescription());
            task.setTenantId(ptask.getTenantId());
            task.setAssignee(assignee);
            task.setName(ptask.getName());
            task.setParentTaskId(ptaskId);
            task.setProcessDefinitionId(ptask.getProcessDefinitionId());
            task.setProcessInstanceId(ptask.getProcessInstanceId());
            task.setTaskDefinitionKey(ptask.getTaskDefinitionKey());
            task.setTaskDefinitionId(ptask.getTaskDefinitionId());
            task.setPriority(ptask.getPriority());
            task.setCreateTime(new Date());
            taskService.saveTask(task);
        }
        return task;
    }

    /**
     * 指派下一任务审批人
     *
     * @param bpmnModel    bpmn模型
     * @param processInsId 流程实例id
     * @param userIds      用户ids
     */
    private void assignNextUsers(BpmnModel bpmnModel, String processInsId, String userIds) {
        // 获取所有节点信息
        List<Task> list = taskService.createTaskQuery().processInstanceId(processInsId).list();
        if (list.size() == 0) {
            return;
        }
        Queue<String> assignIds = CollUtil.newLinkedList(userIds.split(","));
        if (list.size() == assignIds.size()) {
            for (Task task : list) {
                taskService.setAssignee(task.getId(), assignIds.poll());
            }
            return;
        }
        // 优先处理非多实例任务
        Iterator<Task> iterator = list.iterator();
        while (iterator.hasNext()) {
            Task task = iterator.next();
            if (!ModelUtils.isMultiInstance(bpmnModel, task.getTaskDefinitionKey())) {
                if (!assignIds.isEmpty()) {
                    taskService.setAssignee(task.getId(), assignIds.poll());
                }
                iterator.remove();
            }
        }
        // 若存在多实例任务，则进行动态加减签
        if (CollUtil.isNotEmpty(list)) {
            if (assignIds.isEmpty()) {
                // 动态减签
                for (Task task : list) {
                    runtimeService.deleteMultiInstanceExecution(task.getExecutionId(), true);
                }
            } else {
                // 动态加签
                for (String assignId : assignIds) {
                    Map<String, Object> assignVariables = Collections.singletonMap(BpmnXMLConstants.ATTRIBUTE_TASK_USER_ASSIGNEE, assignId);
                    runtimeService.addMultiInstanceExecution(list.get(0).getTaskDefinitionKey(), list.get(0).getProcessInstanceId(), assignVariables);
                }
            }
        }
    }

    /**
     * 获取多实例节点审批人参数
     *
     * @param processDefinitionId
     * @param actId
     * @return
     */
    public String getMultiInstanceActAssigneeParam(String processDefinitionId, String actId) {
        AtomicReference<String> resultParam = new AtomicReference<>();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
        //获取bpmnModel并转为modelNode
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
        //获取主流程
        Process mainProcess = bpmnModel.getMainProcess();
        //获取用户任务节点类型，深入子流程
        mainProcess.findFlowElementsOfType(UserTask.class, true).forEach(userTask -> {
            String userTaskId = userTask.getId();
            if (userTaskId.equals(actId)) {
                Object behavior = userTask.getBehavior();
                if (ObjectUtil.isNotNull(behavior)) {
                    //并行多实例节点
                    if (behavior instanceof ParallelMultiInstanceBehavior) {
                        ParallelMultiInstanceBehavior parallelMultiInstanceBehavior = (ParallelMultiInstanceBehavior) behavior;
                        String collectionElementVariable = parallelMultiInstanceBehavior.getCollectionElementVariable();
                        if (ObjectUtil.isNotEmpty(collectionElementVariable)) {
                            resultParam.set(collectionElementVariable);
                        }
                    }
                    //串行多实例节点
                    if (behavior instanceof SequentialMultiInstanceBehavior) {
                        SequentialMultiInstanceBehavior sequentialMultiInstanceBehavior = (SequentialMultiInstanceBehavior) behavior;
                        String collectionElementVariable = sequentialMultiInstanceBehavior.getCollectionElementVariable();
                        if (ObjectUtil.isNotEmpty(collectionElementVariable)) {
                            resultParam.set(collectionElementVariable);
                        }
                    }
                }
            }
        });
        return resultParam.get();
    }
}
