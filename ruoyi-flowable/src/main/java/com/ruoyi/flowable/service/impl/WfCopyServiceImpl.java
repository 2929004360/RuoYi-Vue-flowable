package com.ruoyi.flowable.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.flowable.constant.ProcessConstants;
import com.ruoyi.flowable.api.domain.WfBusinessProcess;
import com.ruoyi.flowable.domain.WfCopy;
import com.ruoyi.flowable.domain.WfModelProcdef;
import com.ruoyi.flowable.domain.bo.WfCopyBo;
import com.ruoyi.flowable.api.domain.bo.WfTaskBo;
import com.ruoyi.flowable.domain.vo.WfCopyVo;
import com.ruoyi.flowable.mapper.WfCopyMapper;
import com.ruoyi.flowable.page.PageQuery;
import com.ruoyi.flowable.page.TableDataInfo;
import com.ruoyi.flowable.service.IWfBusinessProcessService;
import com.ruoyi.flowable.service.IWfCopyService;
import com.ruoyi.flowable.service.IWfModelProcdefService;
import com.ruoyi.flowable.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.HistoryService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 流程抄送Service业务层处理
 *
 * @author fengcheng
 * @date 2022-05-19
 */
@RequiredArgsConstructor
@Service
public class WfCopyServiceImpl implements IWfCopyService {
    private final WfCopyMapper baseMapper;
    private final HistoryService historyService;

    @Lazy
    private final IWfModelProcdefService wfModelProcdefService;

    @Lazy
    private final IWfBusinessProcessService wfBusinessProcessService;

    /**
     * 查询流程抄送
     *
     * @param copyId 流程抄送主键
     * @return 流程抄送
     */
    @Override
    public WfCopyVo queryById(Long copyId) {
        return baseMapper.selectVoById(copyId);
    }

    /**
     * 查询流程抄送列表
     *
     * @param bo 流程抄送
     * @return 流程抄送
     */
    @Override
    public TableDataInfo<WfCopyVo> selectPageList(WfCopyBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<WfCopy> lqw = buildQueryWrapper(bo);
        lqw.orderByDesc(WfCopy::getCreateTime);
        Page<WfCopyVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        List<WfCopyVo> records = result.getRecords();
        for (WfCopyVo record : records) {
            WfModelProcdef wfModelProcdef = wfModelProcdefService.selectWfModelProcdefByProcdefId(record.getDeploymentId());
            record.setFormType(wfModelProcdef.getFormType());
            record.setFormViewPath(wfModelProcdef.getFormViewPath());

            WfBusinessProcess wfBusinessProcess = wfBusinessProcessService.selectWfBusinessProcessByProcessId(record.getInstanceId());

            if (ObjectUtil.isNotNull(wfBusinessProcess)) {
                record.setBusinessId(wfBusinessProcess.getBusinessId());
            }

            HistoricVariableInstance processStatusVariable = historyService.createHistoricVariableInstanceQuery().processInstanceId(record.getInstanceId()).variableName(ProcessConstants.PROCESS_STATUS_KEY).singleResult();
            String processStatus = null;
            if (ObjectUtil.isNotNull(processStatusVariable)) {
                processStatus = Convert.toStr(processStatusVariable.getValue());
            }
            record.setProcessStatus(processStatus);
        }
        return TableDataInfo.build(result);
    }

    /**
     * 查询流程抄送列表
     *
     * @param bo 流程抄送
     * @return 流程抄送
     */
    @Override
    public List<WfCopyVo> selectList(WfCopyBo bo) {
        LambdaQueryWrapper<WfCopy> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<WfCopy> buildQueryWrapper(WfCopyBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<WfCopy> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getUserId() != null, WfCopy::getUserId, bo.getUserId());
        lqw.like(StringUtils.isNotBlank(bo.getProcessName()), WfCopy::getProcessName, bo.getProcessName());
        lqw.like(StringUtils.isNotBlank(bo.getOriginatorName()), WfCopy::getOriginatorName, bo.getOriginatorName());
        return lqw;
    }

    @Override
    public Boolean makeCopy(WfTaskBo taskBo) {
        if (StringUtils.isBlank(taskBo.getCopyUserIds())) {
            // 若抄送用户为空，则不需要处理，返回成功
            return true;
        }
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(taskBo.getProcInsId()).singleResult();
        String[] ids = taskBo.getCopyUserIds().split("," );
        List<WfCopy> copyList = new ArrayList<>(ids.length);
        Long originatorId = SecurityUtils.getUserId();
        String originatorName = SecurityUtils.getLoginUser().getUser().getNickName();
        String title = historicProcessInstance.getProcessDefinitionName() + "-" + taskBo.getTaskName();
        for (String id : ids) {
            Long userId = Long.valueOf(id);
            WfCopy copy = new WfCopy();
            copy.setTitle(title);
            copy.setProcessId(historicProcessInstance.getProcessDefinitionId());
            copy.setProcessName(historicProcessInstance.getProcessDefinitionName());
            copy.setDeploymentId(historicProcessInstance.getDeploymentId());
            copy.setInstanceId(taskBo.getProcInsId());
            copy.setTaskId(taskBo.getTaskId());
            copy.setUserId(userId);
            copy.setOriginatorId(originatorId);
            copy.setOriginatorName(originatorName);
            copy.setCreateTime(DateUtils.getNowDate());
            copyList.add(copy);
        }
        return baseMapper.insertBatch(copyList);
    }

    /**
     * 删除抄送列表
     *
     * @param copyIds 抄送id
     * @return
     */
    @Override
    public void deleteCopy(String[] copyIds) {
        baseMapper.deleteBatchIds(Arrays.asList(copyIds));
    }
}
