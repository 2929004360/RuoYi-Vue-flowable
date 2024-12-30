package com.ruoyi.flowable.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.flowable.constant.ProcessConstants;
import com.ruoyi.flowable.core.domain.ProcessQuery;
import com.ruoyi.flowable.domain.Deploy;
import com.ruoyi.flowable.domain.WfCopy;
import com.ruoyi.flowable.domain.WfDeployForm;
import com.ruoyi.flowable.domain.WfIcon;
import com.ruoyi.flowable.domain.vo.WfDeployVo;
import com.ruoyi.common.enums.ProcessStatus;
import com.ruoyi.flowable.mapper.WfCopyMapper;
import com.ruoyi.flowable.mapper.WfDeployFormMapper;
import com.ruoyi.flowable.mapper.WfDeployMapper;
import com.ruoyi.flowable.page.PageQuery;
import com.ruoyi.flowable.page.TableDataInfo;
import com.ruoyi.flowable.service.*;
import com.ruoyi.flowable.utils.ProcessUtils;
import lombok.RequiredArgsConstructor;
import org.flowable.common.engine.impl.db.SuspensionState;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fengcheng
 * @createTime 2022/6/30 9:04
 */
@RequiredArgsConstructor
@Service
public class WfDeployServiceImpl implements IWfDeployService {
    private final RepositoryService repositoryService;
    private final WfDeployFormMapper deployFormMapper;

    private final WfCopyMapper wfCopyMapper;

    private final IWfIconService wfIconService;

    private final IWfModelService wfModelService;

    private final WfDeployMapper wfDeployMapper;

    private final IWfModelProcdefService wfModelProcdefService;

    private final IWfFlowMenuService wfFlowMenuService;

    protected final HistoryService historyService;

    @Override
    public TableDataInfo<WfDeployVo> queryPageList(ProcessQuery processQuery, PageQuery pageQuery) {
        // 流程定义列表数据查询
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery()
                .latestVersion()
                .orderByProcessDefinitionKey()
                .asc();
        // 构建搜索条件
        ProcessUtils.buildProcessSearch(processDefinitionQuery, processQuery);
        long pageTotal = processDefinitionQuery.count();
        if (pageTotal <= 0) {
            return TableDataInfo.build();
        }
        int offset = pageQuery.getPageSize() * (pageQuery.getPageNum() - 1);
        List<ProcessDefinition> definitionList = processDefinitionQuery.listPage(offset, pageQuery.getPageSize());
        List<WfDeployVo> deployVoList = new ArrayList<>(definitionList.size());
        for (ProcessDefinition processDefinition : definitionList) {
            String deploymentId = processDefinition.getDeploymentId();
            Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(deploymentId).singleResult();
            WfIcon wfIcon = wfIconService.selectWfIconByDeploymentId(deploymentId);

            WfDeployVo vo = new WfDeployVo();
            vo.setDefinitionId(processDefinition.getId());
            vo.setIcon(wfIcon.getIcon());
            vo.setProcessKey(processDefinition.getKey());
            vo.setProcessName(processDefinition.getName());
            vo.setVersion(processDefinition.getVersion());
            vo.setCategory(processDefinition.getCategory());
            vo.setDeploymentId(processDefinition.getDeploymentId());
            vo.setSuspended(processDefinition.isSuspended());
            // 流程部署信息
            vo.setCategory(deployment.getCategory());
            vo.setDeploymentTime(deployment.getDeploymentTime());
            vo.setDeploymentTime(deployment.getDeploymentTime());
            deployVoList.add(vo);
        }
        Page<WfDeployVo> page = new Page<>();
        page.setRecords(deployVoList);
        page.setTotal(pageTotal);
        return TableDataInfo.build(page);
    }

    @Override
    public TableDataInfo<WfDeployVo> queryPublishList(String processKey, PageQuery pageQuery) {
        // 创建查询条件
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(processKey)
                .orderByProcessDefinitionVersion()
                .desc();
        long pageTotal = processDefinitionQuery.count();
        if (pageTotal <= 0) {
            return TableDataInfo.build();
        }
        // 根据查询条件，查询所有版本
        int offset = pageQuery.getPageSize() * (pageQuery.getPageNum() - 1);
        List<ProcessDefinition> processDefinitionList = processDefinitionQuery
                .listPage(offset, pageQuery.getPageSize());
        List<WfDeployVo> deployVoList = processDefinitionList.stream().map(item -> {
            WfDeployVo vo = new WfDeployVo();
            vo.setDefinitionId(item.getId());
            vo.setProcessKey(item.getKey());
            vo.setProcessName(item.getName());
            vo.setVersion(item.getVersion());
            vo.setCategory(item.getCategory());
            vo.setDeploymentId(item.getDeploymentId());
            vo.setSuspended(item.isSuspended());
            return vo;
        }).collect(Collectors.toList());
        Page<WfDeployVo> page = new Page<>();
        page.setRecords(deployVoList);
        page.setTotal(pageTotal);
        return TableDataInfo.build(page);
    }

    /**
     * 激活或挂起流程
     *
     * @param state        状态
     * @param definitionId 流程定义ID
     */
    @Override
    public void updateState(String definitionId, String state) {
        if (SuspensionState.ACTIVE.toString().equals(state)) {
            // 激活
            repositoryService.activateProcessDefinitionById(definitionId, true, null);
        } else if (SuspensionState.SUSPENDED.toString().equals(state)) {
            // 挂起
            repositoryService.suspendProcessDefinitionById(definitionId, true, null);
        }
    }

    @Override
    public String queryBpmnXmlById(String definitionId) {
        InputStream inputStream = repositoryService.getProcessModel(definitionId);
        try {
            return IoUtil.readUtf8(inputStream);
        } catch (IORuntimeException exception) {
            throw new RuntimeException("加载xml文件异常");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByIds(List<String> deployIds) {
        for (String deployId : deployIds) {
            // 获取进行中的流程
            HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery().deploymentId(deployId).orderByProcessInstanceStartTime().desc();
            List<HistoricProcessInstance> historicProcessInstances = historicProcessInstanceQuery.list();
            for (HistoricProcessInstance hisIns : historicProcessInstances) {
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

                // 判断流程状态等于进行中不能删除
                if(ProcessStatus.RUNNING.getStatus().equals(processStatus)){
                    throw new RuntimeException("[["+hisIns.getProcessDefinitionName()+"]]流程正在进行中，不能删除");
                }
            }

            // 删除图标
            wfIconService.deleteWfIconByDeploymentId(deployId);

            // 删除部署
            repositoryService.deleteDeployment(deployId, true);

            // 删除模型关联部署数据
            wfModelProcdefService.deleteWfModelProcdefByProcdefId(deployId);

            // 删除抄送数据
            deployFormMapper.delete(new LambdaQueryWrapper<WfDeployForm>().eq(WfDeployForm::getDeployId, deployId));
            wfCopyMapper.delete(new LambdaQueryWrapper<WfCopy>().eq(WfCopy::getDeploymentId, deployId));
        }
    }

    /**
     * 查询流程部署列表
     *
     * @param processQuery
     * @param procdefIdList
     * @return
     */
    @Override
    public List<WfDeployVo> selectWfDeployList(ProcessQuery processQuery, List<String> procdefIdList) {
        List<Deploy> list = wfDeployMapper.selectWfDeployList(processQuery, procdefIdList);
        List<WfDeployVo> wfDeployVoList = new ArrayList<>();
        for (Deploy deploy : list) {
            String deploymentId = deploy.getDeploymentId();
            Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(deploymentId).singleResult();
            WfIcon wfIcon = wfIconService.selectWfIconByDeploymentId(deploymentId);

            WfDeployVo vo = new WfDeployVo();

            vo.setDefinitionId(deploy.getId());
            vo.setIcon(wfIcon.getIcon());
            vo.setProcessKey(deploy.getKey());
            vo.setProcessName(deploy.getName());
            vo.setVersion(deploy.getVersion());
            vo.setCategory(deploy.getCategory());
            vo.setDeploymentId(deploy.getDeploymentId());
            vo.setSuspended(deploy.getSuspensionState() == 1);
            // 流程部署信息
            vo.setCategory(deployment.getCategory());
            vo.setDeploymentTime(deployment.getDeploymentTime());
            vo.setDeploymentTime(deployment.getDeploymentTime());
            wfDeployVoList.add(vo);
        }

        return wfDeployVoList;
    }
}
