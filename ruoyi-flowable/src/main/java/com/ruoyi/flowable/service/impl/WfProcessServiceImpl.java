package com.ruoyi.flowable.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.DataException;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.flowable.api.domain.WfBusinessProcess;
import com.ruoyi.flowable.constant.ProcessConstants;
import com.ruoyi.flowable.constant.TaskConstants;
import com.ruoyi.flowable.core.FormConf;
import com.ruoyi.flowable.core.domain.ProcessQuery;
import com.ruoyi.flowable.domain.*;
import com.ruoyi.flowable.domain.bo.DdToBpmn;
import com.ruoyi.flowable.domain.bo.ResubmitProcess;
import com.ruoyi.flowable.domain.bo.WfModelBo;
import com.ruoyi.flowable.domain.vo.*;
import com.ruoyi.flowable.enums.FormType;
import com.ruoyi.common.enums.ProcessStatus;
import com.ruoyi.flowable.factory.FlowServiceFactory;
import com.ruoyi.flowable.flow.FlowableUtils;
import com.ruoyi.flowable.handler.BusinessProcessDetailsHandler;
import com.ruoyi.flowable.handler.DeleteProcessBusinessHandler;
import com.ruoyi.flowable.handler.ResubmitProcessHandler;
import com.ruoyi.flowable.mapper.WfDeployFormMapper;
import com.ruoyi.flowable.mapper.WfProcessMapper;
import com.ruoyi.flowable.page.PageQuery;
import com.ruoyi.flowable.page.TableDataInfo;
import com.ruoyi.flowable.service.*;
import com.ruoyi.flowable.utils.*;
import com.ruoyi.system.service.ISysDeptService;
import com.ruoyi.system.service.ISysRoleService;
import com.ruoyi.system.service.ISysUserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.flowable.bpmn.BpmnAutoLayout;
import org.flowable.bpmn.constants.BpmnXMLConstants;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricActivityInstanceQuery;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.task.Comment;
import org.flowable.identitylink.api.history.HistoricIdentityLink;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fengcheng
 */
@RequiredArgsConstructor
@Service
public class WfProcessServiceImpl extends FlowServiceFactory implements IWfProcessService {

    @Lazy
    private final IWfRoamHistoricalService wfRoamHistoricalService;

    @Lazy
    private final IWfTaskService wfTaskService;

    @Lazy
    private final ISysUserService sysUserService;

    @Lazy
    private final ISysRoleService sysRoleService;

    @Lazy
    private final ISysDeptService sysDeptService;

    @Lazy
    private final WfDeployFormMapper deployFormMapper;

    @Lazy
    private final IWfIconService wfIconService;

    @Lazy
    private final WfProcessMapper wfProcessMapper;

    @Lazy
    private final IWfModelService wfModelService;

    @Lazy
    private final IWfModelProcdefService wfModelProcdefService;

    /**
     * 仿钉钉流程转bpmn用
     */
    @Lazy
    private BpmnModel ddBpmnModel;

    @Lazy
    private Process ddProcess;

    @Lazy
    private List<SequenceFlow> ddSequenceFlows;

    @Lazy
    private final IWfFlowMenuService wfFlowMenuService;

    @Lazy
    private final DeleteProcessBusinessHandler deleteProcessBusinessHandler;

    @Lazy
    private final BusinessProcessDetailsHandler businessProcessDetailsHandler;

    @Lazy
    private final IWfBusinessProcessService wfBusinessProcessService;

    @Lazy
    private final ResubmitProcessHandler resubmitProcessHandler;

    /**
     * 流程定义列表
     *
     * @param processQuery 查询参数
     * @return 流程定义分页列表数据
     */
    @Override
    public List<WfDefinitionVo> selectPageStartProcessList(ProcessQuery processQuery) {
        List<Deploy> list = new ArrayList<>();
        if (SecurityUtils.hasRole("admin" )) {
            list = wfProcessMapper.selectProcessList(processQuery, null);
        } else {
            List<WfModelVo> wfModelVoList = wfModelService.selectList(new WfModelBo());
            List<String> modelIdList = wfModelVoList.stream().map(WfModelVo::getModelId).collect(Collectors.toList());
            if (modelIdList.size() == 0) {
                return new ArrayList<>();
            }
            List<String> procdefIdList = wfModelProcdefService.selectWfModelProcdefListByModelIdList(modelIdList);

            list = wfProcessMapper.selectProcessList(processQuery, procdefIdList);
        }


        List<WfDefinitionVo> definitionVoList = new ArrayList<>();
        for (Deploy deploy : list) {
            String deploymentId = deploy.getDeploymentId();
            Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(deploymentId).singleResult();
            WfIcon wfIcon = wfIconService.selectWfIconByDeploymentId(deploymentId);

            WfDefinitionVo vo = new WfDefinitionVo();
            vo.setDefinitionId(deploy.getId());
            vo.setIcon(wfIcon.getIcon());
            vo.setProcessKey(deploy.getKey());
            vo.setProcessName(deploy.getName());
            vo.setVersion(deploy.getVersion());
            vo.setDeploymentId(deploy.getDeploymentId());
            vo.setSuspended(deploy.getSuspensionState() == 1);
            vo.setFormType(deploy.getFormType());
            vo.setFormCreatePath(deploy.getFormCreatePath());
            // 流程定义时间
            vo.setCategory(deployment.getCategory());
            vo.setDeploymentTime(deployment.getDeploymentTime());
            definitionVoList.add(vo);
        }
        return definitionVoList;
    }

    @Override
    public List<WfDefinitionVo> selectStartProcessList(ProcessQuery processQuery) {
        // 流程定义列表数据查询
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery().latestVersion().active().orderByProcessDefinitionKey().asc();
        // 构建搜索条件
        ProcessUtils.buildProcessSearch(processDefinitionQuery, processQuery);
        List<ProcessDefinition> definitionList = processDefinitionQuery.list();
        List<WfDefinitionVo> definitionVoList = new ArrayList<>();
        for (ProcessDefinition processDefinition : definitionList) {
            String deploymentId = processDefinition.getDeploymentId();
            Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(deploymentId).singleResult();
            WfDefinitionVo vo = new WfDefinitionVo();
            vo.setDefinitionId(processDefinition.getId());
            vo.setProcessKey(processDefinition.getKey());
            vo.setProcessName(processDefinition.getName());
            vo.setVersion(processDefinition.getVersion());
            vo.setDeploymentId(processDefinition.getDeploymentId());
            vo.setSuspended(processDefinition.isSuspended());
            // 流程定义时间
            vo.setCategory(deployment.getCategory());
            vo.setDeploymentTime(deployment.getDeploymentTime());
            definitionVoList.add(vo);
        }
        return definitionVoList;
    }

    @Override
    public TableDataInfo<WfTaskVo> selectPageOwnProcessList(ProcessQuery processQuery, PageQuery pageQuery) {
        Page<WfTaskVo> page = new Page<>();
        HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery().orderByProcessInstanceStartTime().desc();
        if (!SecurityUtils.hasRole("admin" )) {
            historicProcessInstanceQuery.startedBy(TaskUtils.getUserId());
        }

        if (ObjectUtil.isNotNull(processQuery.getIsComplete()) && !processQuery.getIsComplete()) {
            historicProcessInstanceQuery.unfinished();
        }

        // 构建搜索条件
        ProcessUtils.buildProcessSearch(historicProcessInstanceQuery, processQuery);
        int offset = pageQuery.getPageSize() * (pageQuery.getPageNum() - 1);
        List<HistoricProcessInstance> historicProcessInstances = historicProcessInstanceQuery.listPage(offset, pageQuery.getPageSize());
        page.setTotal(historicProcessInstanceQuery.count());


        List<WfTaskVo> taskVoList = new ArrayList<>();
        for (HistoricProcessInstance hisIns : historicProcessInstances) {
            WfTaskVo taskVo = new WfTaskVo();
            // 获取流程状态
            HistoricVariableInstance processStatusVariable = historyService.createHistoricVariableInstanceQuery().processInstanceId(hisIns.getId()).variableName(ProcessConstants.PROCESS_STATUS_KEY).singleResult();
            String processStatus = null;
            if (ObjectUtil.isNotNull(processStatusVariable)) {
                processStatus = Convert.toStr(processStatusVariable.getValue());
            }
            // 兼容旧流程
            if (processStatus == null) {
                processStatus = ObjectUtil.isNull(hisIns.getEndTime()) ? ProcessStatus.RUNNING.getStatus() : ProcessStatus.COMPLETED.getStatus();
            }
            taskVo.setProcessStatus(processStatus);
            taskVo.setCreateTime(hisIns.getStartTime());
            taskVo.setFinishTime(hisIns.getEndTime());
            taskVo.setProcInsId(hisIns.getId());
            WfBusinessProcess wfBusinessProcess = wfBusinessProcessService.selectWfBusinessProcessByProcessId(hisIns.getId());

            if (ObjectUtil.isNotNull(wfBusinessProcess)) {
                taskVo.setBusinessId(wfBusinessProcess.getBusinessId());
            }
            // 计算耗时
            if (Objects.nonNull(hisIns.getEndTime())) {
                taskVo.setDuration(DateUtils.getDatePoor(hisIns.getEndTime(), hisIns.getStartTime()));
            } else {
                taskVo.setDuration(DateUtils.getDatePoor(DateUtils.getNowDate(), hisIns.getStartTime()));
            }
            // 流程部署实例信息
            Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(hisIns.getDeploymentId()).singleResult();
            taskVo.setDeployId(hisIns.getDeploymentId());
            WfModelProcdef wfModelProcdef = wfModelProcdefService.selectWfModelProcdefByProcdefId(hisIns.getDeploymentId());

            taskVo.setProcDefId(hisIns.getProcessDefinitionId());
            taskVo.setFormType(wfModelProcdef.getFormType());
            taskVo.setFormViewPath(wfModelProcdef.getFormViewPath());
            taskVo.setFormCreatePath(wfModelProcdef.getFormCreatePath());
            taskVo.setProcDefName(hisIns.getProcessDefinitionName());
            taskVo.setProcDefVersion(hisIns.getProcessDefinitionVersion());
            taskVo.setCategory(deployment.getCategory());
            // 当前所处流程
            List<Task> taskList = taskService.createTaskQuery().processInstanceId(hisIns.getId()).includeIdentityLinks().list();
            if (CollUtil.isNotEmpty(taskList)) {
                taskVo.setTaskName(taskList.stream().map(Task::getName).filter(StringUtils::isNotEmpty).collect(Collectors.joining("," )));
            }
            taskVoList.add(taskVo);
        }
        page.setRecords(taskVoList);
        return TableDataInfo.build(page);
    }

    @Override
    public List<WfTaskVo> selectOwnProcessList(ProcessQuery processQuery) {
        HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery().startedBy(TaskUtils.getUserId()).orderByProcessInstanceStartTime().desc();
        // 构建搜索条件
        ProcessUtils.buildProcessSearch(historicProcessInstanceQuery, processQuery);
        List<HistoricProcessInstance> historicProcessInstances = historicProcessInstanceQuery.list();
        List<WfTaskVo> taskVoList = new ArrayList<>();
        for (HistoricProcessInstance hisIns : historicProcessInstances) {
            WfTaskVo taskVo = new WfTaskVo();
            taskVo.setCreateTime(hisIns.getStartTime());
            taskVo.setFinishTime(hisIns.getEndTime());
            taskVo.setProcInsId(hisIns.getId());
            // 计算耗时
            if (Objects.nonNull(hisIns.getEndTime())) {
                taskVo.setDuration(DateUtils.getDatePoor(hisIns.getEndTime(), hisIns.getStartTime()));
            } else {
                taskVo.setDuration(DateUtils.getDatePoor(DateUtils.getNowDate(), hisIns.getStartTime()));
            }
            // 流程部署实例信息
            Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(hisIns.getDeploymentId()).singleResult();
            taskVo.setDeployId(hisIns.getDeploymentId());
            taskVo.setProcDefId(hisIns.getProcessDefinitionId());
            taskVo.setProcDefName(hisIns.getProcessDefinitionName());
            taskVo.setProcDefVersion(hisIns.getProcessDefinitionVersion());
            taskVo.setCategory(deployment.getCategory());
            // 当前所处流程
            List<Task> taskList = taskService.createTaskQuery().processInstanceId(hisIns.getId()).includeIdentityLinks().list();
            if (CollUtil.isNotEmpty(taskList)) {
                taskVo.setTaskName(taskList.stream().map(Task::getName).filter(StringUtils::isNotEmpty).collect(Collectors.joining("," )));
            }
            taskVoList.add(taskVo);
        }
        return taskVoList;
    }

    @Override
    public TableDataInfo<WfTaskVo> selectPageTodoProcessList(ProcessQuery processQuery, PageQuery pageQuery) {
        Page<WfTaskVo> page = new Page<>();

        TaskQuery taskQuery = taskService.createTaskQuery().active().includeProcessVariables().orderByTaskCreateTime().desc();
        if (!SecurityUtils.hasRole("admin" )) {
            taskQuery.taskCandidateOrAssigned(TaskUtils.getUserId());
            taskQuery.taskCandidateGroupIn(TaskUtils.getCandidateGroup());
        }
        // 构建搜索条件
        ProcessUtils.buildProcessSearch(taskQuery, processQuery);
        page.setTotal(taskQuery.count());
        int offset = pageQuery.getPageSize() * (pageQuery.getPageNum() - 1);
        List<Task> taskList = taskQuery.listPage(offset, pageQuery.getPageSize());
        List<WfTaskVo> flowList = new ArrayList<>();
        for (Task task : taskList) {
            WfTaskVo flowTask = new WfTaskVo();
            // 当前流程信息
            flowTask.setTaskId(task.getId());
            flowTask.setParentTaskId(task.getParentTaskId());
            flowTask.setTaskDefKey(task.getTaskDefinitionKey());
            flowTask.setCreateTime(task.getCreateTime());
            flowTask.setProcDefId(task.getProcessDefinitionId());
            flowTask.setTaskName(task.getName());
            // 流程定义信息
            ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().processDefinitionId(task.getProcessDefinitionId()).singleResult();
            WfModelProcdef wfModelProcdef = wfModelProcdefService.selectWfModelProcdefByProcdefId(pd.getDeploymentId());
            flowTask.setFormType(wfModelProcdef.getFormType());
            flowTask.setFormViewPath(wfModelProcdef.getFormViewPath());

            flowTask.setDeployId(pd.getDeploymentId());
            flowTask.setProcDefName(pd.getName());
            flowTask.setProcDefVersion(pd.getVersion());
            flowTask.setProcInsId(task.getProcessInstanceId());
            // 流程发起人信息
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
            if (ObjectUtil.isNotNull(historicProcessInstance)) {
                Long userId = Long.parseLong(historicProcessInstance.getStartUserId());
                SysUser sysUser = sysUserService.selectUserById(userId);
                String nickName = sysUser.getNickName();
                flowTask.setStartUserId(userId);
                flowTask.setStartUserName(nickName);
                // 流程变量
                flowTask.setProcVars(task.getProcessVariables());
                flowList.add(flowTask);
            }
        }
        page.setRecords(flowList);
        return TableDataInfo.build(page);
    }

    @Override
    public List<WfTaskVo> selectTodoProcessList(ProcessQuery processQuery) {
        TaskQuery taskQuery = taskService.createTaskQuery().active().includeProcessVariables().taskCandidateOrAssigned(TaskUtils.getUserId()).taskCandidateGroupIn(TaskUtils.getCandidateGroup()).orderByTaskCreateTime().desc();
        // 构建搜索条件
        ProcessUtils.buildProcessSearch(taskQuery, processQuery);
        List<Task> taskList = taskQuery.list();
        List<WfTaskVo> taskVoList = new ArrayList<>();
        for (Task task : taskList) {
            WfTaskVo taskVo = new WfTaskVo();
            // 当前流程信息
            taskVo.setTaskId(task.getId());
            taskVo.setTaskDefKey(task.getTaskDefinitionKey());
            taskVo.setCreateTime(task.getCreateTime());
            taskVo.setProcDefId(task.getProcessDefinitionId());
            taskVo.setTaskName(task.getName());
            // 流程定义信息
            ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().processDefinitionId(task.getProcessDefinitionId()).singleResult();
            taskVo.setDeployId(pd.getDeploymentId());
            taskVo.setProcDefName(pd.getName());
            taskVo.setProcDefVersion(pd.getVersion());
            taskVo.setProcInsId(task.getProcessInstanceId());
            // 流程发起人信息
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
            Long userId = Long.parseLong(historicProcessInstance.getStartUserId());
            SysUser sysUser = sysUserService.selectUserById(userId);
            String nickName = sysUser.getNickName();
            taskVo.setStartUserId(userId);
            taskVo.setStartUserName(nickName);

            taskVoList.add(taskVo);
        }
        return taskVoList;
    }

    /**
     * 查询待签任务列表
     *
     * @param processQuery
     * @param pageQuery    分页参数
     * @return
     */
    @Override
    public TableDataInfo<WfTaskVo> selectPageClaimProcessList(ProcessQuery processQuery, PageQuery pageQuery) {
        Page<WfTaskVo> page = new Page<>();
        TaskQuery taskQuery = taskService.createTaskQuery().active().includeProcessVariables().orderByTaskCreateTime().desc();

        if (!SecurityUtils.hasRole("admin" )) {
            taskQuery.taskCandidateUser(TaskUtils.getUserId());
            taskQuery.taskCandidateGroupIn(TaskUtils.getCandidateGroup());
        }

        // 构建搜索条件
        ProcessUtils.buildProcessSearch(taskQuery, processQuery);
        page.setTotal(taskQuery.count());
        int offset = pageQuery.getPageSize() * (pageQuery.getPageNum() - 1);
        List<Task> taskList = taskQuery.listPage(offset, pageQuery.getPageSize());
        List<WfTaskVo> flowList = new ArrayList<>();
        for (Task task : taskList) {
            WfTaskVo flowTask = new WfTaskVo();
            // 当前流程信息
            flowTask.setTaskId(task.getId());
            flowTask.setTaskDefKey(task.getTaskDefinitionKey());
            flowTask.setCreateTime(task.getCreateTime());
            flowTask.setProcDefId(task.getProcessDefinitionId());
            flowTask.setTaskName(task.getName());
            // 流程定义信息
            ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().processDefinitionId(task.getProcessDefinitionId()).singleResult();
            flowTask.setDeployId(pd.getDeploymentId());
            flowTask.setProcDefName(pd.getName());
            flowTask.setProcDefVersion(pd.getVersion());
            flowTask.setProcInsId(task.getProcessInstanceId());
            // 流程发起人信息
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
            if (ObjectUtil.isNotNull(historicProcessInstance)) {
                Long userId = Long.parseLong(historicProcessInstance.getStartUserId());
                SysUser sysUser = sysUserService.selectUserById(userId);
                String nickName = sysUser.getNickName();
                flowTask.setStartUserId(userId);
                flowTask.setStartUserName(nickName);
            }
            flowList.add(flowTask);
        }
        page.setRecords(flowList);
        return TableDataInfo.build(page);
    }

    @Override
    public List<WfTaskVo> selectClaimProcessList(ProcessQuery processQuery) {
        TaskQuery taskQuery = taskService.createTaskQuery().active().includeProcessVariables().taskCandidateUser(TaskUtils.getUserId()).taskCandidateGroupIn(TaskUtils.getCandidateGroup()).orderByTaskCreateTime().desc();
        // 构建搜索条件
        ProcessUtils.buildProcessSearch(taskQuery, processQuery);
        List<Task> taskList = taskQuery.list();
        List<WfTaskVo> flowList = new ArrayList<>();
        for (Task task : taskList) {
            WfTaskVo flowTask = new WfTaskVo();
            // 当前流程信息
            flowTask.setTaskId(task.getId());
            flowTask.setTaskDefKey(task.getTaskDefinitionKey());
            flowTask.setCreateTime(task.getCreateTime());
            flowTask.setProcDefId(task.getProcessDefinitionId());
            flowTask.setTaskName(task.getName());
            // 流程定义信息
            ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().processDefinitionId(task.getProcessDefinitionId()).singleResult();
            flowTask.setDeployId(pd.getDeploymentId());
            flowTask.setProcDefName(pd.getName());
            flowTask.setProcDefVersion(pd.getVersion());
            flowTask.setProcInsId(task.getProcessInstanceId());
            // 流程发起人信息
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
            Long userId = Long.parseLong(historicProcessInstance.getStartUserId());
            SysUser sysUser = sysUserService.selectUserById(userId);
            String nickName = sysUser.getNickName();
            flowTask.setStartUserId(userId);
            flowTask.setStartUserName(nickName);

            flowList.add(flowTask);
        }
        return flowList;
    }

    @Override
    public TableDataInfo<WfTaskVo> selectPageFinishedProcessList(ProcessQuery processQuery, PageQuery pageQuery) {
        Page<WfTaskVo> page = new Page<>();
        HistoricTaskInstanceQuery taskInstanceQuery = historyService.createHistoricTaskInstanceQuery().includeProcessVariables().finished().orderByHistoricTaskInstanceEndTime().desc();

        if (!SecurityUtils.hasRole("admin" )) {
            taskInstanceQuery.taskAssignee(TaskUtils.getUserId());
        }

        // 构建搜索条件
        ProcessUtils.buildProcessSearch(taskInstanceQuery, processQuery);
        int offset = pageQuery.getPageSize() * (pageQuery.getPageNum() - 1);
        List<HistoricTaskInstance> historicTaskInstanceList = taskInstanceQuery.listPage(offset, pageQuery.getPageSize());
        List<WfTaskVo> hisTaskList = new ArrayList<>();
        for (HistoricTaskInstance histTask : historicTaskInstanceList) {
            WfTaskVo flowTask = new WfTaskVo();
            // 当前流程信息
            flowTask.setTaskId(histTask.getId());
            // 审批人员信息
            flowTask.setCreateTime(histTask.getCreateTime());
            flowTask.setFinishTime(histTask.getEndTime());
            flowTask.setDuration(DateUtil.formatBetween(histTask.getDurationInMillis(), BetweenFormatter.Level.SECOND));
            flowTask.setProcDefId(histTask.getProcessDefinitionId());
            flowTask.setTaskDefKey(histTask.getTaskDefinitionKey());
            flowTask.setTaskName(histTask.getName());
            // 流程定义信息
            ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().processDefinitionId(histTask.getProcessDefinitionId()).singleResult();
            flowTask.setDeployId(pd.getDeploymentId());
            flowTask.setProcDefName(pd.getName());
            flowTask.setProcDefVersion(pd.getVersion());
            flowTask.setProcInsId(histTask.getProcessInstanceId());
            flowTask.setHisProcInsId(histTask.getProcessInstanceId());

            WfModelProcdef wfModelProcdef = wfModelProcdefService.selectWfModelProcdefByProcdefId(pd.getDeploymentId());
            flowTask.setFormType(wfModelProcdef.getFormType());
            flowTask.setFormViewPath(wfModelProcdef.getFormViewPath());

            WfBusinessProcess wfBusinessProcess = wfBusinessProcessService.selectWfBusinessProcessByProcessId(histTask.getProcessInstanceId());

            if (ObjectUtil.isNotNull(wfBusinessProcess)) {
                flowTask.setBusinessId(wfBusinessProcess.getBusinessId());
            }

            HistoricVariableInstance processStatusVariable = historyService.createHistoricVariableInstanceQuery().processInstanceId(histTask.getProcessInstanceId()).variableName(ProcessConstants.PROCESS_STATUS_KEY).singleResult();
            String processStatus = null;
            if (ObjectUtil.isNotNull(processStatusVariable)) {
                processStatus = Convert.toStr(processStatusVariable.getValue());
            }
            // 兼容旧流程
            if (processStatus == null) {
                processStatus = ObjectUtil.isNull(histTask.getEndTime()) ? ProcessStatus.RUNNING.getStatus() : ProcessStatus.COMPLETED.getStatus();
            }
            flowTask.setProcessStatus(processStatus);

            // 流程发起人信息
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(histTask.getProcessInstanceId()).singleResult();
            if (ObjectUtil.isNotNull(historicProcessInstance)) {
                Long userId = Long.parseLong(historicProcessInstance.getStartUserId());
                SysUser sysUser = sysUserService.selectUserById(userId);
                String nickName = sysUser.getNickName();
                flowTask.setStartUserId(userId);
                flowTask.setStartUserName(nickName);
                // 流程变量
                flowTask.setProcVars(histTask.getProcessVariables());
                hisTaskList.add(flowTask);
            }
        }
        page.setTotal(taskInstanceQuery.count());
        page.setRecords(hisTaskList);
//        Map<String, Object> result = new HashMap<>();
//        result.put("result",page);
//        result.put("finished",true);
        return TableDataInfo.build(page);
    }

    @Override
    public List<WfTaskVo> selectFinishedProcessList(ProcessQuery processQuery) {
        HistoricTaskInstanceQuery taskInstanceQuery = historyService.createHistoricTaskInstanceQuery().includeProcessVariables().finished().taskAssignee(TaskUtils.getUserId()).orderByHistoricTaskInstanceEndTime().desc();
        // 构建搜索条件
        ProcessUtils.buildProcessSearch(taskInstanceQuery, processQuery);
        List<HistoricTaskInstance> historicTaskInstanceList = taskInstanceQuery.list();
        List<WfTaskVo> hisTaskList = new ArrayList<>();
        for (HistoricTaskInstance histTask : historicTaskInstanceList) {
            WfTaskVo flowTask = new WfTaskVo();
            // 当前流程信息
            flowTask.setTaskId(histTask.getId());
            // 审批人员信息
            flowTask.setCreateTime(histTask.getCreateTime());
            flowTask.setFinishTime(histTask.getEndTime());
            flowTask.setDuration(DateUtil.formatBetween(histTask.getDurationInMillis(), BetweenFormatter.Level.SECOND));
            flowTask.setProcDefId(histTask.getProcessDefinitionId());
            flowTask.setTaskDefKey(histTask.getTaskDefinitionKey());
            flowTask.setTaskName(histTask.getName());
            // 流程定义信息
            ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().processDefinitionId(histTask.getProcessDefinitionId()).singleResult();
            flowTask.setDeployId(pd.getDeploymentId());
            flowTask.setProcDefName(pd.getName());
            flowTask.setProcDefVersion(pd.getVersion());
            flowTask.setProcInsId(histTask.getProcessInstanceId());
            flowTask.setHisProcInsId(histTask.getProcessInstanceId());
            // 流程发起人信息
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(histTask.getProcessInstanceId()).singleResult();
            Long userId = Long.parseLong(historicProcessInstance.getStartUserId());
            SysUser sysUser = sysUserService.selectUserById(userId);
            String nickName = sysUser.getNickName();
            flowTask.setStartUserId(userId);
            flowTask.setStartUserName(nickName);
            // 流程变量
            flowTask.setProcVars(histTask.getProcessVariables());
            hisTaskList.add(flowTask);
        }
        return hisTaskList;
    }

    @Override
    public FormConf selectFormContent(String definitionId, String deployId, String procInsId) {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(definitionId);
        if (ObjectUtil.isNull(bpmnModel)) {
            throw new RuntimeException("获取流程设计失败！");
        }
        StartEvent startEvent = ModelUtils.getStartEvent(bpmnModel);
        WfDeployForm deployForm = deployFormMapper.selectOne(new LambdaQueryWrapper<WfDeployForm>().eq(WfDeployForm::getDeployId, deployId).eq(WfDeployForm::getFormKey, startEvent.getFormKey()).eq(WfDeployForm::getNodeKey, startEvent.getId()));
        FormConf formConf = JsonUtils.parseObject(deployForm.getContent(), FormConf.class);
        if (ObjectUtil.isNull(formConf)) {
            throw new RuntimeException("获取流程表单失败！");
        }
        if (ObjectUtil.isNotEmpty(procInsId)) {
            // 获取流程实例
            HistoricProcessInstance historicProcIns = historyService.createHistoricProcessInstanceQuery().processInstanceId(procInsId).includeProcessVariables().singleResult();
            // 填充表单信息
            ProcessFormUtils.fillFormData(formConf, historicProcIns.getProcessVariables());
        }
        return formConf;
    }

    /**
     * 根据流程定义ID启动流程实例
     *
     * @param procDefId 流程定义Id
     * @param variables 流程变量
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProcessInstance startProcessByDefId(String procDefId, Map<String, Object> variables) {
        try {
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(procDefId).singleResult();
            return startProcess(processDefinition, variables);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("流程启动错误" );
        }
    }

    /**
     * 通过DefinitionKey启动流程
     *
     * @param procDefKey 流程定义Key
     * @param variables  扩展参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startProcessByDefKey(String procDefKey, Map<String, Object> variables) {
        try {
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(procDefKey).latestVersion().singleResult();
            startProcess(processDefinition, variables);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("流程启动错误" );
        }
    }

    /**
     * 删除流程实例
     *
     * @param instanceIds
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProcessByIds(String[] instanceIds) {
        List<String> ids = Arrays.asList(instanceIds);

        if (!SecurityUtils.hasRole("admin" )) {
            // 校验流程是否结束
            long activeInsCount = runtimeService.createProcessInstanceQuery().processInstanceIds(new HashSet<>(ids)).active().count();
            if (activeInsCount > 0) {
                throw new ServiceException("不允许删除进行中的流程实例" );
            }
        }
        // 删除历史流程实例
        historyService.bulkDeleteHistoricProcessInstances(ids);

        // 删除业务流程
        deleteProcessBusinessHandler.delete(ids);
    }

    /**
     * 读取xml文件
     *
     * @param processDefId 流程定义ID
     */
    @Override
    public String queryBpmnXmlById(String processDefId) {
        InputStream inputStream = repositoryService.getProcessModel(processDefId);
        try {
            return IoUtil.readUtf8(inputStream);
        } catch (IORuntimeException exception) {
            throw new RuntimeException("加载xml文件异常" );
        }
    }

    /**
     * 流程详情信息
     *
     * @param procInsId 流程实例ID
     * @param taskId    任务ID
     * @param formType  表单类型
     * @return
     */
    @Override
    public WfDetailVo queryProcessDetail(String procInsId, String taskId, String formType) {
        WfDetailVo detailVo = new WfDetailVo();
        // 获取流程实例
        HistoricProcessInstance historicProcIns = historyService.createHistoricProcessInstanceQuery().processInstanceId(procInsId).includeProcessVariables().singleResult();
        if (StringUtils.isNotBlank(taskId)) {
            HistoricTaskInstance taskIns = historyService.createHistoricTaskInstanceQuery().taskId(taskId).includeIdentityLinks().includeProcessVariables().includeTaskLocalVariables().singleResult();
            if (taskIns == null) {
                throw new ServiceException("没有可办理的任务！" );
            }
            detailVo.setTaskFormData(currTaskFormData(historicProcIns.getDeploymentId(), taskIns));
        }
        // 获取Bpmn模型信息
        InputStream inputStream = repositoryService.getProcessModel(historicProcIns.getProcessDefinitionId());
        String bpmnXmlStr = StrUtil.utf8Str(IoUtil.readBytes(inputStream, false));
        BpmnModel bpmnModel = ModelUtils.getBpmnModel(bpmnXmlStr);

        detailVo.setBpmnXml(bpmnXmlStr);
        String buttonList = getButtonList(bpmnModel, procInsId);
        if (StringUtils.isNotEmpty(buttonList)) {
            detailVo.setButtonList(Arrays.asList(Objects.requireNonNull(buttonList).split("," )));
        }
        // 历史流程节点信息
        detailVo.setHistoryProcNodeList(historyProcNodeList(historicProcIns));

        if (FormType.PROCESS.getType().equals(formType)) {
            detailVo.setProcessFormList(processFormList(bpmnModel, historicProcIns));
        } else {
            // 设置业务流程详情
            businessProcessDetailsHandler.setBusinessProcess(detailVo, historicProcIns.getProcessVariables());

            // 历史流程审批节点信息
            Map<String, Object> processVariables = historicProcIns.getProcessVariables();
            String businessId = processVariables.get(ProcessConstants.BUSINESS_ID).toString();
            WfRoamHistorical wfRoamHistorical = new WfRoamHistorical();
            wfRoamHistorical.setBusinessId(businessId);
            detailVo.setHistoryApproveProcNodeList(wfRoamHistoricalService.selectWfRoamHistoricalList(wfRoamHistorical));
        }
        detailVo.setFlowViewer(getFlowViewer(bpmnModel, procInsId));
        return detailVo;
    }

    /**
     * 根据钉钉流程json转flowable的bpmn的xml格式
     *
     * @param ddToBpmn
     * @return
     */
    @Override
    public R<String> dingdingToBpmn(DdToBpmn ddToBpmn) {
        try {
            String ddjson = ddToBpmn.getJson();
            JSONObject object = JSON.parseObject(ddjson, JSONObject.class);
            //JSONObject workflow = object.getJSONObject("process");
            //ddBpmnModel.addProcess(ddProcess);
            //ddProcess.setName (workflow.getString("name"));
            //ddProcess.setId(workflow.getString("processId"));
            ddProcess = new Process();
            ddBpmnModel = new BpmnModel();
            ddSequenceFlows = Lists.newArrayList();
            ddBpmnModel.addProcess(ddProcess);
            ddProcess.setId("Process_" + UUID.randomUUID());
            ddProcess.setName(ddToBpmn.getName());
            JSONObject flowNode = object.getJSONObject("processData" );
            StartEvent startEvent = createStartEvent(flowNode);
            ddProcess.addFlowElement(startEvent);
            String lastNode = create(startEvent.getId(), flowNode);
            EndEvent endEvent = createEndEvent();
            ddProcess.addFlowElement(endEvent);
            ddProcess.addFlowElement(connect(lastNode, endEvent.getId()));

            new BpmnAutoLayout(ddBpmnModel).execute();
            return R.ok(new String(new BpmnXMLConverter().convertToXML(ddBpmnModel)));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("创建失败: e=" + e.getMessage());
        }
    }

    /**
     * 查询可发起列表
     *
     * @return
     */
    private List<WfDefinitionVo> selectWfDefinitionListByModelIdList(List<String> modelIdList) {
        if (modelIdList.size() == 0) {
            return new ArrayList<>();
        }
        List<String> procdefIdList = wfModelProcdefService.selectWfModelProcdefListByModelIdList(modelIdList);

        if (procdefIdList.size() == 0) {
            throw new RuntimeException("没有流程部署,请先部署流程！" );
        }

        List<Deploy> list = wfProcessMapper.selectProcessList(new ProcessQuery(), procdefIdList);

        List<WfDefinitionVo> definitionVoList = new ArrayList<>();
        for (Deploy deploy : list) {
            String deploymentId = deploy.getDeploymentId();
            Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(deploymentId).singleResult();
            WfIcon wfIcon = wfIconService.selectWfIconByDeploymentId(deploymentId);

            WfDefinitionVo vo = new WfDefinitionVo();
            vo.setDefinitionId(deploy.getId());
            vo.setIcon(wfIcon.getIcon());
            vo.setProcessKey(deploy.getKey());
            vo.setProcessName(deploy.getName());
            vo.setVersion(deploy.getVersion());
            vo.setDeploymentId(deploy.getDeploymentId());
            vo.setSuspended(deploy.getSuspensionState() == 1);
            vo.setFormType(deploy.getFormType());
            vo.setFormCreatePath(deploy.getFormCreatePath());
            // 流程定义时间
            vo.setCategory(deployment.getCategory());
            vo.setDeploymentTime(deployment.getDeploymentTime());
            definitionVoList.add(vo);
        }
        return definitionVoList;
    }

    /**
     * 根据菜单id获取可发起列表
     *
     * @param menuId
     * @return
     */
    @Override
    public List<WfDefinitionVo> getStartList(String menuId) {
        WfFlowMenu wfFlowMenu = new WfFlowMenu();
        wfFlowMenu.setMenuId(menuId);
        wfFlowMenuService.selectWfFlowMenuList(wfFlowMenu);
        if (wfFlowMenuService.selectWfFlowMenuList(wfFlowMenu).size() == 0) {
            throw new DataException("请先配置请假表单流程！" );
        }

        // 校验流程模型
        List<WfModelVo> wfModelVoList = wfModelService.getModelByMenuId(menuId);
        if (wfModelVoList.size() == 0) {
            throw new DataException("没有流程模型,请先配置流程模型！" );
        }

        // 校验流程部署
        List<String> modelIdList = wfModelVoList.stream().map(WfModelVo::getModelId).collect(Collectors.toList());
        return selectWfDefinitionListByModelIdList(modelIdList);
    }

    /**
     * 重新发起流程实例
     *
     * @param resubmitProcess 重新发起
     */
    @Override
    public void resubmitProcess(ResubmitProcess resubmitProcess) {
        WfBusinessProcess wfBusinessProcess = wfBusinessProcessService.selectWfBusinessProcessByProcessId(resubmitProcess.getProcInsId());
        resubmitProcessHandler.resubmit(wfBusinessProcess);
    }

    StartEvent createStartEvent(JSONObject flowNode) {
        String nodeType = flowNode.getString("type" );
        StartEvent startEvent = new StartEvent();
        startEvent.setId(id("start" ));
        if (Type.INITIATOR_TASK.isEqual(nodeType)) {
            JSONObject properties = flowNode.getJSONObject("properties" );
            if (StringUtils.isNotEmpty(properties.getString("formKey" ))) {
                startEvent.setFormKey(properties.getString("formKey" ));
            }
        }
        return startEvent;
    }

    String id(String prefix) {
        return prefix + "_" + UUID.randomUUID().toString().replace("-" , "" ).toLowerCase();
    }

    enum Type {

        /**
         * 并行事件
         */
        CONCURRENT("concurrent" , ParallelGateway.class),

        /**
         * 排他事件
         */
        EXCLUSIVE("exclusive" , ExclusiveGateway.class),

        /**
         * 服务任务
         */
        SERVICE_TASK("serviceTask" , ServiceTask.class),

        /**
         * 发起人任务
         */
        INITIATOR_TASK("start" , ServiceTask.class),

        /**
         * 审批任务
         */
        APPROVER_TASK("approver" , ServiceTask.class),

        /**
         * 用户任务
         */
        USER_TASK("userTask" , UserTask.class);

        private String type;

        private Class<?> typeClass;

        Type(String type, Class<?> typeClass) {
            this.type = type;
            this.typeClass = typeClass;
        }

        public final static Map<String, Class<?>> TYPE_MAP = Maps.newHashMap();

        static {
            for (Type element : Type.values()) {
                TYPE_MAP.put(element.type, element.typeClass);
            }
        }

        public boolean isEqual(String type) {
            return this.type.equals(type);
        }

    }

    String create(String fromId, JSONObject flowNode) throws InvocationTargetException, IllegalAccessException {
        String nodeType = flowNode.getString("type" );

        if (Type.INITIATOR_TASK.isEqual(nodeType)) {
            flowNode.put("incoming" , Collections.singletonList(fromId));
            String id = createUserTask(flowNode, nodeType);

            if (flowNode.containsKey("concurrentNodes" )) { //并行网关
                return createConcurrentGatewayBuilder(id, flowNode);
            }

            if (flowNode.containsKey("conditionNodes" )) { //排它网关或叫条件网关
                return createExclusiveGatewayBuilder(id, flowNode);
            }

            // 如果当前任务还有后续任务，则遍历创建后续任务
            JSONObject nextNode = flowNode.getJSONObject("childNode" );
            if (Objects.nonNull(nextNode)) {
                FlowElement flowElement = ddBpmnModel.getFlowElement(id);
                return create(id, nextNode);
            } else {
                return id;
            }
        } else if (Type.USER_TASK.isEqual(nodeType) || Type.APPROVER_TASK.isEqual(nodeType)) {
            flowNode.put("incoming" , Collections.singletonList(fromId));
            String id = createUserTask(flowNode, nodeType);
            if (flowNode.containsKey("concurrentNodes" )) { //并行网关
                return createConcurrentGatewayBuilder(id, flowNode);
            }

            if (flowNode.containsKey("conditionNodes" )) { //排它网关或叫条件网关
                return createExclusiveGatewayBuilder(id, flowNode);
            }

            // 如果当前任务还有后续任务，则遍历创建后续任务
            JSONObject nextNode = flowNode.getJSONObject("childNode" );
            if (Objects.nonNull(nextNode)) {
                FlowElement flowElement = ddBpmnModel.getFlowElement(id);
                return create(id, nextNode);
            } else {
                return id;
            }
        } else if (Type.SERVICE_TASK.isEqual(nodeType)) {
            flowNode.put("incoming" , Collections.singletonList(fromId));
            String id = createServiceTask(flowNode);

            if (flowNode.containsKey("concurrentNodes" )) { //并行网关
                return createConcurrentGatewayBuilder(id, flowNode);
            }

            if (flowNode.containsKey("conditionNodes" )) { //排它网关或叫条件网关
                return createExclusiveGatewayBuilder(id, flowNode);
            }

            // 如果当前任务还有后续任务，则遍历创建后续任务
            JSONObject nextNode = flowNode.getJSONObject("childNode" );
            if (Objects.nonNull(nextNode)) {
                FlowElement flowElement = ddBpmnModel.getFlowElement(id);
                return create(id, nextNode);
            } else {
                return id;
            }
        } else {
            throw new RuntimeException("未知节点类型: nodeType=" + nodeType);
        }
    }

    EndEvent createEndEvent() {
        EndEvent endEvent = new EndEvent();
        endEvent.setId(id("end" ));
        return endEvent;
    }

    SequenceFlow connect(String from, String to) {
        SequenceFlow flow = new SequenceFlow();
        flow.setId(id("sequenceFlow" ));
        flow.setSourceRef(from);
        flow.setTargetRef(to);
        ddSequenceFlows.add(flow);
        return flow;
    }

    String createUserTask(JSONObject flowNode, String nodeType) {
        List<String> incoming = flowNode.getJSONArray("incoming" ).toJavaList(String.class);
        // 自动生成id
        String id = id("userTask" );
        if (incoming != null && !incoming.isEmpty()) {
            UserTask userTask = new UserTask();
            JSONObject properties = flowNode.getJSONObject("properties" );
            userTask.setName(properties.getString("title" ));
            userTask.setId(id);
            List<ExtensionAttribute> attributes = new ArrayList<ExtensionAttribute>();
            if (Type.INITIATOR_TASK.isEqual(nodeType)) {
                ExtensionAttribute extAttribute = new ExtensionAttribute();
                extAttribute.setNamespace(ProcessConstants.NAMASPASE);
                extAttribute.setName("dataType" );
                extAttribute.setValue("INITIATOR" );
                attributes.add(extAttribute);
                userTask.addAttribute(extAttribute);
                userTask.setAssignee("${initiator}" );
            } else if (Type.USER_TASK.isEqual(nodeType) || Type.APPROVER_TASK.isEqual(nodeType)) {
                String assignType = properties.getString("assigneeType" );
                if (StringUtils.equalsAnyIgnoreCase("user" , assignType)) {
                    JSONArray approvers = properties.getJSONArray("approvers" );
                    JSONObject approver = approvers.getJSONObject(0);
                    ExtensionAttribute extDataTypeAttribute = new ExtensionAttribute();
                    extDataTypeAttribute.setNamespace(ProcessConstants.NAMASPASE);
                    extDataTypeAttribute.setName("dataType" );
                    extDataTypeAttribute.setValue("USERS" );
                    userTask.addAttribute(extDataTypeAttribute);
                    ExtensionAttribute extTextAttribute = new ExtensionAttribute();
                    extTextAttribute.setNamespace(ProcessConstants.NAMASPASE);
                    extTextAttribute.setName("text" );
                    extTextAttribute.setValue(approver.getString("nickName" ));
                    userTask.addAttribute(extTextAttribute);
                    userTask.setFormKey(properties.getString("formKey" ));
                    userTask.setAssignee(approver.getString("userName" ));
                } else if (StringUtils.equalsAnyIgnoreCase("director" , assignType)) {
                    ExtensionAttribute extDataTypeAttribute = new ExtensionAttribute();
                    extDataTypeAttribute.setNamespace(ProcessConstants.NAMASPASE);
                    extDataTypeAttribute.setName("dataType" );
                    extDataTypeAttribute.setValue("MANAGER" );
                    userTask.addAttribute(extDataTypeAttribute);
                    ExtensionAttribute extTextAttribute = new ExtensionAttribute();
                    extTextAttribute.setNamespace(ProcessConstants.NAMASPASE);
                    extTextAttribute.setName("text" );
                    extTextAttribute.setValue("部门经理" );
                    userTask.addAttribute(extTextAttribute);
                    userTask.setFormKey(properties.getString("formKey" ));
                    userTask.setAssignee("${DepManagerHandler.getUser(execution)}" );
                } else if (StringUtils.equalsAnyIgnoreCase("role" , assignType)) {
                    JSONArray approvers = properties.getJSONArray("approvers" );
                    JSONObject approver = approvers.getJSONObject(0);
                    ExtensionAttribute extDataTypeAttribute = new ExtensionAttribute();
                    extDataTypeAttribute.setNamespace(ProcessConstants.NAMASPASE);
                    extDataTypeAttribute.setName("dataType" );
                    extDataTypeAttribute.setValue("ROLES" );
                    userTask.addAttribute(extDataTypeAttribute);
                    ExtensionAttribute extTextAttribute = new ExtensionAttribute();
                    extTextAttribute.setNamespace(ProcessConstants.NAMASPASE);
                    extTextAttribute.setName("text" );
                    extTextAttribute.setValue(approver.getString("roleName" ));
                    userTask.addAttribute(extTextAttribute);
                    userTask.setFormKey(properties.getString("formKey" ));
                    List<SysRole> sysroleslist = approvers.toJavaList(SysRole.class);
                    List<String> roleslist = sysroleslist.stream().map(e -> e.getRoleKey()).collect(Collectors.toList());
                    userTask.setCandidateGroups(roleslist);
                    userTask.setAssignee("${assignee}" );
                    MultiInstanceLoopCharacteristics loopCharacteristics = new MultiInstanceLoopCharacteristics();
                    if (StringUtils.equalsAnyIgnoreCase(properties.getString("counterSign" ), "true" )) {//并行会签
                        loopCharacteristics.setSequential(false);
                        loopCharacteristics.setInputDataItem("${multiInstanceHandler.getUserNames(execution)}" );
                        loopCharacteristics.setElementVariable("assignee" );
                        loopCharacteristics.setCompletionCondition("${nrOfCompletedInstances &gt;= nrOfInstances}" );
                    } else {
                        loopCharacteristics.setSequential(false);
                        loopCharacteristics.setInputDataItem("${multiInstanceHandler.getUserNames(execution)}" );
                        loopCharacteristics.setElementVariable("assignee" );
                        loopCharacteristics.setCompletionCondition("${nrOfCompletedInstances &gt; 0}" );
                    }
                    userTask.setLoopCharacteristics(loopCharacteristics);
                }
            }

            ddProcess.addFlowElement(userTask);
            ddProcess.addFlowElement(connect(incoming.get(0), id));
        }
        return id;
    }

    String createConcurrentGatewayBuilder(String fromId, JSONObject flowNode) throws InvocationTargetException, IllegalAccessException {
        //String name = flowNode.getString("nodeName");
        //下面创建并行网关并进行连线
        ParallelGateway parallelGateway = new ParallelGateway();
        String parallelGatewayId = id("parallelGateway" );
        parallelGateway.setId(parallelGatewayId);
        parallelGateway.setName("并行网关" );
        ddProcess.addFlowElement(parallelGateway);
        ddProcess.addFlowElement(connect(fromId, parallelGatewayId));

        if (Objects.isNull(flowNode.getJSONArray("concurrentNodes" )) && Objects.isNull(flowNode.getJSONObject("childNode" ))) {
            return parallelGatewayId;
        }

        //获取并行列表数据
        List<JSONObject> flowNodes = Optional.ofNullable(flowNode.getJSONArray("concurrentNodes" )).map(e -> e.toJavaList(JSONObject.class)).orElse(Collections.emptyList());
        List<String> incoming = Lists.newArrayListWithCapacity(flowNodes.size());
        for (JSONObject element : flowNodes) {
            JSONObject childNode = element.getJSONObject("childNode" );
            if (Objects.isNull(childNode)) {//没子节点,就把并行id加入入口队列
                incoming.add(parallelGatewayId);
                continue;
            }
            String identifier = create(parallelGatewayId, childNode);
            if (Objects.nonNull(identifier)) {//否则加入有子节点的用户id
                incoming.add(identifier);
            }
        }

        JSONObject childNode = flowNode.getJSONObject("childNode" );

        if (Objects.nonNull(childNode)) {
            // 普通结束网关
            if (CollectionUtils.isEmpty(incoming)) {
                return create(parallelGatewayId, childNode);
            } else {
                // 所有 user task 连接 end parallel gateway
                childNode.put("incoming" , incoming);
                FlowElement flowElement = ddBpmnModel.getFlowElement(incoming.get(0));
                // 1.0 先进行边连接, 暂存 nextNode
                JSONObject nextNode = childNode.getJSONObject("childNode" );
                childNode.put("childNode" , null); //不加这个,下面创建子节点会进入递归了
                String identifier = create(incoming.get(0), childNode);
                for (int i = 1; i < incoming.size(); i++) {//其中0之前创建的时候已经连接过了，所以从1开始补另外一条
                    FlowElement flowElementIncoming = ddBpmnModel.getFlowElement(incoming.get(i));
                    ddProcess.addFlowElement(connect(flowElementIncoming.getId(), identifier));
                }
                // 1.1 边连接完成后，在进行 nextNode 创建
                if (Objects.nonNull(nextNode)) {
                    return create(identifier, nextNode);
                } else {
                    return identifier;
                }
            }
        }
        if (incoming.size() > 0) {
            return incoming.get(1);
        } else {
            return parallelGatewayId;
        }

    }

    String createExclusiveGatewayBuilder(String formId, JSONObject flowNode) throws InvocationTargetException, IllegalAccessException {
        //String name = flowNode.getString("nodeName");
        String exclusiveGatewayId = id("exclusiveGateway" );
        ExclusiveGateway exclusiveGateway = new ExclusiveGateway();
        exclusiveGateway.setId(exclusiveGatewayId);
        exclusiveGateway.setName("排它条件网关" );
        ddProcess.addFlowElement(exclusiveGateway);
        ddProcess.addFlowElement(connect(formId, exclusiveGatewayId));

        if (Objects.isNull(flowNode.getJSONArray("conditionNodes" )) && Objects.isNull(flowNode.getJSONObject("childNode" ))) {
            return exclusiveGatewayId;
        }
        List<JSONObject> flowNodes = Optional.ofNullable(flowNode.getJSONArray("conditionNodes" )).map(e -> e.toJavaList(JSONObject.class)).orElse(Collections.emptyList());
        List<String> incoming = Lists.newArrayListWithCapacity(flowNodes.size());

        List<JSONObject> conditions = Lists.newCopyOnWriteArrayList();
        for (JSONObject element : flowNodes) {
            JSONObject childNode = element.getJSONObject("childNode" );
            JSONObject properties = element.getJSONObject("properties" );
            String nodeName = properties.getString("title" );
            String expression = properties.getString("conditions" );

            if (Objects.isNull(childNode)) {
                incoming.add(exclusiveGatewayId);
                JSONObject condition = new JSONObject();
                condition.fluentPut("nodeName" , nodeName).fluentPut("expression" , expression);
                conditions.add(condition);
                continue;
            }
            // 只生成一个任务，同时设置当前任务的条件
            childNode.put("incoming" , Collections.singletonList(exclusiveGatewayId));
            String identifier = create(exclusiveGatewayId, childNode);
            List<SequenceFlow> flows = ddSequenceFlows.stream().filter(flow -> StringUtils.equals(exclusiveGatewayId, flow.getSourceRef())).collect(Collectors.toList());

            flows.stream().forEach(e -> {
                if (StringUtils.isBlank(e.getName()) && StringUtils.isNotBlank(nodeName)) {
                    e.setName(nodeName);
                }
                // 设置条件表达式
                if (Objects.isNull(e.getConditionExpression()) && StringUtils.isNotBlank(expression)) {
                    e.setConditionExpression(expression);
                }
            });
            if (Objects.nonNull(identifier)) {
                incoming.add(identifier);
            }
        }


        JSONObject childNode = flowNode.getJSONObject("childNode" );
        if (Objects.nonNull(childNode)) {
            if (incoming == null || incoming.isEmpty()) {
                return create(exclusiveGatewayId, childNode);
            } else {
                // 所有 service task 连接 end exclusive gateway
                childNode.put("incoming" , incoming);
                FlowElement flowElement = ddBpmnModel.getFlowElement(incoming.get(0));
                // 1.0 先进行边连接, 暂存 nextNode
                JSONObject nextNode = childNode.getJSONObject("childNode" );
                childNode.put("childNode" , null);
                String identifier = create(flowElement.getId(), childNode);
                for (int i = 1; i < incoming.size(); i++) {
                    ddProcess.addFlowElement(connect(incoming.get(i), identifier));
                }

                //  针对 gateway 空任务分支 添加条件表达式
                if (!conditions.isEmpty()) {
                    FlowElement flowElement1 = ddBpmnModel.getFlowElement(identifier);
                    // 获取从 gateway 到目标节点 未设置条件表达式的节点
                    List<SequenceFlow> flows = ddSequenceFlows.stream().filter(flow -> StringUtils.equals(flowElement1.getId(), flow.getTargetRef())).filter(flow -> StringUtils.equals(flow.getSourceRef(), exclusiveGatewayId)).collect(Collectors.toList());
                    flows.stream().forEach(sequenceFlow -> {
                        if (!conditions.isEmpty()) {
                            JSONObject condition = conditions.get(0);
                            String nodeName = condition.getString("content" );
                            String expression = condition.getString("expression" );

                            if (StringUtils.isBlank(sequenceFlow.getName()) && StringUtils.isNotBlank(nodeName)) {
                                sequenceFlow.setName(nodeName);
                            }
                            // 设置条件表达式
                            if (Objects.isNull(sequenceFlow.getConditionExpression()) && StringUtils.isNotBlank(expression)) {
                                sequenceFlow.setConditionExpression(expression);
                            }

                            conditions.remove(0);
                        }
                    });

                }

                // 1.1 边连接完成后，在进行 nextNode 创建
                if (Objects.nonNull(nextNode)) {
                    return create(identifier, nextNode);
                } else {
                    return identifier;
                }
            }
        }
        return exclusiveGatewayId;
    }

    String createServiceTask(JSONObject flowNode) {
        List<String> incoming = flowNode.getJSONArray("incoming" ).toJavaList(String.class);
        // 自动生成id
        String id = id("serviceTask" );
        if (incoming != null && !incoming.isEmpty()) {
            ServiceTask serviceTask = new ServiceTask();
            serviceTask.setName(flowNode.getString("nodeName" ));
            serviceTask.setId(id);
            ddProcess.addFlowElement(serviceTask);
            ddProcess.addFlowElement(connect(incoming.get(0), id));
        }
        return id;
    }

    /**
     * 获取节点显示的按钮
     *
     * @param bpmnModel
     * @param procInsId
     */
    private String getButtonList(BpmnModel bpmnModel, String procInsId) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(procInsId).singleResult();
        if (ObjectUtil.isNotNull(processInstance)) {
            String currentActivityId = runtimeService.getActiveActivityIds(processInstance.getId()).get(0);

            if (ObjectUtil.isNotNull(bpmnModel) && CollectionUtil.isNotEmpty(bpmnModel.getProcesses())) {
                Process process = bpmnModel.getProcesses().get(0);
                Collection<FlowElement> flowElements = process.getFlowElements();
                for (FlowElement flowElement : flowElements) {
                    if (flowElement instanceof UserTask) {
                        UserTask userTask = (UserTask) flowElement;
                        if (currentActivityId.equals(userTask.getId())) {
                            List<ExtensionAttribute> extensionAttributes = userTask.getAttributes().get(ProcessConstants.BUTTON_LIST);
                            if (ObjectUtil.isNotNull(extensionAttributes)) {
                                ExtensionAttribute extensionAttribute = extensionAttributes.get(0);
                                return extensionAttribute.getValue();
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * 启动流程实例
     */
    private ProcessInstance startProcess(ProcessDefinition procDef, Map<String, Object> variables) {
        if (ObjectUtil.isNotNull(procDef) && procDef.isSuspended()) {
            throw new ServiceException("流程已被挂起，请先激活流程" );
        }
        // 设置流程发起人Id到流程中
        String userIdStr = TaskUtils.getUserId();
        //设置已认证的用户ID
        identityService.setAuthenticatedUserId(userIdStr);
        variables.put(BpmnXMLConstants.ATTRIBUTE_EVENT_START_INITIATOR, userIdStr);
        // 设置流程状态为进行中
        variables.put(ProcessConstants.PROCESS_STATUS_KEY, ProcessStatus.RUNNING.getStatus());
        // 发起流程实例
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(procDef.getId(), variables);
        // 第一个用户任务为发起人，则自动完成任务
        wfTaskService.startFirstTask(processInstance, variables);

        return processInstance;
    }


    /**
     * 获取流程变量
     *
     * @param taskId 任务ID
     * @return 流程变量
     */
    private Map<String, Object> getProcessVariables(String taskId) {
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().includeProcessVariables().finished().taskId(taskId).singleResult();
        if (Objects.nonNull(historicTaskInstance)) {
            return historicTaskInstance.getProcessVariables();
        }
        return taskService.getVariables(taskId);
    }

    /**
     * 获取当前任务流程表单信息
     */
    private FormConf currTaskFormData(String deployId, HistoricTaskInstance taskIns) {
        WfDeployFormVo deployFormVo = deployFormMapper.selectVoOne(new LambdaQueryWrapper<WfDeployForm>().eq(WfDeployForm::getDeployId, deployId).eq(WfDeployForm::getFormKey, taskIns.getFormKey()).eq(WfDeployForm::getNodeKey, taskIns.getTaskDefinitionKey()));
        if (ObjectUtil.isNotEmpty(deployFormVo)) {
            FormConf currTaskFormData = JsonUtils.parseObject(deployFormVo.getContent(), FormConf.class);
            if (null != currTaskFormData) {
                currTaskFormData.setFormBtns(false);
                ProcessFormUtils.fillFormData(currTaskFormData, taskIns.getTaskLocalVariables());
                return currTaskFormData;
            }
        }
        return null;
    }

    /**
     * 获取历史流程表单信息
     */
    private List<FormConf> processFormList(BpmnModel bpmnModel, HistoricProcessInstance historicProcIns) {
        List<FormConf> procFormList = new ArrayList<>();
        List<HistoricActivityInstance> activityInstanceList = historyService.createHistoricActivityInstanceQuery().processInstanceId(historicProcIns.getId()).finished().activityTypes(CollUtil.newHashSet(BpmnXMLConstants.ELEMENT_EVENT_START, BpmnXMLConstants.ELEMENT_TASK_USER)).orderByHistoricActivityInstanceStartTime().asc().list();
        List<String> processFormKeys = new ArrayList<>();
        for (HistoricActivityInstance activityInstance : activityInstanceList) {
            // 获取当前节点流程元素信息
            FlowElement flowElement = ModelUtils.getFlowElementById(bpmnModel, activityInstance.getActivityId());
            // 获取当前节点表单Key
            String formKey = ModelUtils.getFormKey(flowElement);
            if (formKey == null) {
                continue;
            }
            boolean localScope = Convert.toBool(ModelUtils.getElementAttributeValue(flowElement, ProcessConstants.PROCESS_FORM_LOCAL_SCOPE), false);
            Map<String, Object> variables;
            if (localScope) {
                // 查询任务节点参数，并转换成Map
                variables = historyService.createHistoricVariableInstanceQuery().processInstanceId(historicProcIns.getId()).taskId(activityInstance.getTaskId()).list().stream().collect(Collectors.toMap(HistoricVariableInstance::getVariableName, HistoricVariableInstance::getValue));
            } else {
                if (processFormKeys.contains(formKey)) {
                    continue;
                }
                variables = historicProcIns.getProcessVariables();
                processFormKeys.add(formKey);
            }
            // 非节点表单此处查询结果可能有多条，只获取第一条信息
            List<WfDeployFormVo> formInfoList = deployFormMapper.selectVoList(new LambdaQueryWrapper<WfDeployForm>().eq(WfDeployForm::getDeployId, historicProcIns.getDeploymentId()).eq(WfDeployForm::getFormKey, formKey).eq(localScope, WfDeployForm::getNodeKey, flowElement.getId()));

            //@update by Brath：避免空集合导致的NULL空指针
            WfDeployFormVo formInfo = formInfoList.stream().findFirst().orElse(null);

            if (ObjectUtil.isNotNull(formInfo)) {
                // 旧数据 formInfo.getFormName() 为 null
                String formName = Optional.ofNullable(formInfo.getFormName()).orElse(StringUtils.EMPTY);
                String title = localScope ? formName.concat("(" + flowElement.getName() + ")" ) : formName;
                FormConf formConf = JsonUtils.parseObject(formInfo.getContent(), FormConf.class);
                if (null != formConf) {
                    formConf.setTitle(title);
                    formConf.setDisabled(true);
                    formConf.setFormBtns(false);
                    ProcessFormUtils.fillFormData(formConf, variables);
                    procFormList.add(formConf);
                }
            }
        }
        return procFormList;
    }

    @Deprecated
    private void buildStartFormData(HistoricProcessInstance historicProcIns, Process process, String deployId, List<FormConf> procFormList) {
        procFormList = procFormList == null ? new ArrayList<>() : procFormList;
        HistoricActivityInstance startInstance = historyService.createHistoricActivityInstanceQuery().processInstanceId(historicProcIns.getId()).activityId(historicProcIns.getStartActivityId()).singleResult();
        StartEvent startEvent = (StartEvent) process.getFlowElement(startInstance.getActivityId());
        WfDeployFormVo startFormInfo = deployFormMapper.selectVoOne(new LambdaQueryWrapper<WfDeployForm>().eq(WfDeployForm::getDeployId, deployId).eq(WfDeployForm::getFormKey, startEvent.getFormKey()).eq(WfDeployForm::getNodeKey, startEvent.getId()));
        if (ObjectUtil.isNotNull(startFormInfo)) {
            FormConf formConf = JsonUtils.parseObject(startFormInfo.getContent(), FormConf.class);
            if (null != formConf) {
                formConf.setTitle(startEvent.getName());
                formConf.setDisabled(true);
                formConf.setFormBtns(false);
                ProcessFormUtils.fillFormData(formConf, historicProcIns.getProcessVariables());
                procFormList.add(formConf);
            }
        }
    }

    @Deprecated
    private void buildUserTaskFormData(String procInsId, String deployId, Process process, List<FormConf> procFormList) {
        procFormList = procFormList == null ? new ArrayList<>() : procFormList;
        List<HistoricActivityInstance> activityInstanceList = historyService.createHistoricActivityInstanceQuery().processInstanceId(procInsId).finished().activityType(BpmnXMLConstants.ELEMENT_TASK_USER).orderByHistoricActivityInstanceStartTime().asc().list();
        for (HistoricActivityInstance instanceItem : activityInstanceList) {
            UserTask userTask = (UserTask) process.getFlowElement(instanceItem.getActivityId(), true);
            String formKey = userTask.getFormKey();
            if (formKey == null) {
                continue;
            }
            // 查询任务节点参数，并转换成Map
            Map<String, Object> variables = historyService.createHistoricVariableInstanceQuery().processInstanceId(procInsId).taskId(instanceItem.getTaskId()).list().stream().collect(Collectors.toMap(HistoricVariableInstance::getVariableName, HistoricVariableInstance::getValue));
            WfDeployFormVo deployFormVo = deployFormMapper.selectVoOne(new LambdaQueryWrapper<WfDeployForm>().eq(WfDeployForm::getDeployId, deployId).eq(WfDeployForm::getFormKey, formKey).eq(WfDeployForm::getNodeKey, userTask.getId()));
            if (ObjectUtil.isNotNull(deployFormVo)) {
                FormConf formConf = JsonUtils.parseObject(deployFormVo.getContent(), FormConf.class);
                if (null != formConf) {
                    formConf.setTitle(userTask.getName());
                    formConf.setDisabled(true);
                    formConf.setFormBtns(false);
                    ProcessFormUtils.fillFormData(formConf, variables);
                    procFormList.add(formConf);
                }
            }
        }
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
                    SysUser sysUser = sysUserService.selectUserById(userId);
                    String nickName = sysUser.getNickName();
                    if (nickName != null) {
                        elementVo.setAssigneeId(userId);
                        elementVo.setAssigneeName(nickName);
                    }
                }
            } else if (BpmnXMLConstants.ELEMENT_TASK_USER.equals(activityInstance.getActivityType())) {
                if (StringUtils.isNotBlank(activityInstance.getAssignee())) {
                    Long userId = Long.parseLong(activityInstance.getAssignee());
                    SysUser sysUser = sysUserService.selectUserById(userId);
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
                            SysUser sysUser = sysUserService.selectUserById(userId);
                            String nickName = sysUser.getNickName();
                            stringBuilder.append(nickName).append("," );
                        }
                        if (StringUtils.isNotBlank(identityLink.getGroupId())) {
                            if (identityLink.getGroupId().startsWith(TaskConstants.ROLE_GROUP_PREFIX)) {
                                Long roleId = Long.parseLong(StringUtils.stripStart(identityLink.getGroupId(), TaskConstants.ROLE_GROUP_PREFIX));
                                SysRole role = sysRoleService.selectRoleById(roleId);
                                stringBuilder.append(role.getRoleName()).append("," );
                            } else if (identityLink.getGroupId().startsWith(TaskConstants.DEPT_GROUP_PREFIX)) {
                                Long deptId = Long.parseLong(StringUtils.stripStart(identityLink.getGroupId(), TaskConstants.DEPT_GROUP_PREFIX));
                                SysDept dept = sysDeptService.selectDeptById(deptId);
                                stringBuilder.append(dept.getDeptName()).append("," );
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

    /**
     * 获取流程执行过程
     *
     * @param procInsId
     * @return
     */
    private WfViewerVo getFlowViewer(BpmnModel bpmnModel, String procInsId) {
        // 构建查询条件
        HistoricActivityInstanceQuery query = historyService.createHistoricActivityInstanceQuery().processInstanceId(procInsId);
        List<HistoricActivityInstance> allActivityInstanceList = query.list();
        if (CollUtil.isEmpty(allActivityInstanceList)) {
            return new WfViewerVo();
        }
        // 查询所有已完成的元素
        List<HistoricActivityInstance> finishedElementList = allActivityInstanceList.stream().filter(item -> ObjectUtil.isNotNull(item.getEndTime())).collect(Collectors.toList());
        // 所有已完成的连线
        Set<String> finishedSequenceFlowSet = new HashSet<>();
        // 所有已完成的任务节点
        Set<String> finishedTaskSet = new HashSet<>();
        finishedElementList.forEach(item -> {
            if (BpmnXMLConstants.ELEMENT_SEQUENCE_FLOW.equals(item.getActivityType())) {
                finishedSequenceFlowSet.add(item.getActivityId());
            } else {
                finishedTaskSet.add(item.getActivityId());
            }
        });
        // 查询所有未结束的节点
        Set<String> unfinishedTaskSet = allActivityInstanceList.stream().filter(item -> ObjectUtil.isNull(item.getEndTime())).map(HistoricActivityInstance::getActivityId).collect(Collectors.toSet());
        // DFS 查询未通过的元素集合
        Set<String> rejectedSet = FlowableUtils.dfsFindRejects(bpmnModel, unfinishedTaskSet, finishedSequenceFlowSet, finishedTaskSet);
        return new WfViewerVo(finishedTaskSet, finishedSequenceFlowSet, unfinishedTaskSet, rejectedSet);
    }
}
