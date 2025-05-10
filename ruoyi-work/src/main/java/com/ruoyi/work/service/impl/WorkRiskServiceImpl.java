package com.ruoyi.work.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.enums.FlowMenuEnum;
import com.ruoyi.common.enums.ProcessStatus;
import com.ruoyi.common.exception.DataException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.flowable.api.domain.WfBusinessProcess;
import com.ruoyi.flowable.api.domain.bo.WfTaskBo;
import com.ruoyi.flowable.api.domain.vo.WorkRiskVo;
import com.ruoyi.flowable.api.service.IWfBusinessProcessServiceApi;
import com.ruoyi.flowable.api.service.IWfProcessServiceApi;
import com.ruoyi.flowable.api.service.IWfTaskServiceApi;
import com.ruoyi.work.mapper.WorkRiskMapper;
import com.ruoyi.work.service.IWorkRiskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

/**
 * 隐患上报Service业务层处理
 *
 * @author fengcheng
 * @date 2025-03-01
 */
@Service
public class WorkRiskServiceImpl implements IWorkRiskService {
    @Autowired
    private WorkRiskMapper workRiskMapper;

    @Autowired
    private IWfBusinessProcessServiceApi wfBusinessProcessServiceApi;

    @Autowired
    private IWfProcessServiceApi processServiceApi;

    @Autowired
    private IWfTaskServiceApi flowTaskServiceApi;

    /**
     * 查询隐患上报
     *
     * @param riskId 隐患上报主键
     * @return 隐患上报
     */
    @Override
    public WorkRiskVo selectWorkRiskByRiskId(Long riskId) {
        return workRiskMapper.selectWorkRiskByRiskId(riskId);
    }

    /**
     * 查询隐患上报列表
     *
     * @param workLeaveVo 隐患上报
     * @param checked     是否包含下级
     * @return 隐患上报
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<WorkRiskVo> selectWorkRiskList(WorkRiskVo workLeaveVo, Boolean checked) {
        return workRiskMapper.selectWorkRiskList(workLeaveVo, checked);
    }

    /**
     * 新增隐患上报
     *
     * @param workRiskVo 隐患上报
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertWorkRisk(WorkRiskVo workRiskVo) {
        workRiskVo.setCreateTime(DateUtils.getNowDate());
        int i = workRiskMapper.insertWorkRisk(workRiskVo);


        workRiskVo.setBusinessId(String.valueOf(workRiskVo.getRiskId()));
        workRiskVo.setBusinessProcessType(FlowMenuEnum.RISK_FLOW_MENU.getCode());

        if (ProcessStatus.RUNNING.getStatus().equals(workRiskVo.getSchedule())) {
            WorkRiskVo workRisk = new WorkRiskVo();
            String processInstanceId = processServiceApi.startProcessByDefId(workRiskVo.getDefinitionId(), BeanUtil.beanToMap(workRiskVo, new HashMap<>(16), false, false));
            WfBusinessProcess wfBusinessProcess = new WfBusinessProcess();
            wfBusinessProcess.setProcessId(processInstanceId);
            wfBusinessProcess.setBusinessId(String.valueOf(workRiskVo.getRiskId()));
            wfBusinessProcess.setBusinessProcessType(FlowMenuEnum.RISK_FLOW_MENU.getCode());
            wfBusinessProcessServiceApi.insertWfBusinessProcess(wfBusinessProcess);
            workRisk.setProcessId(processInstanceId);
            workRisk.setSchedule(ProcessStatus.RUNNING.getStatus());
            workRisk.setRiskId(workRiskVo.getRiskId());
            return workRiskMapper.updateWorkRisk(workRisk);
        }
        return i;
    }

    /**
     * 修改隐患上报
     *
     * @param workRiskVo 隐患上报
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateWorkRisk(WorkRiskVo workRiskVo) {
        checkExistence(workRiskVo.getRiskId());
        workRiskVo.setUpdateTime(DateUtils.getNowDate());

        if (ProcessStatus.RUNNING.getStatus().equals(workRiskVo.getSchedule())) {
            workRiskVo.setBusinessId(String.valueOf(workRiskVo.getRiskId()));
            workRiskVo.setBusinessProcessType(FlowMenuEnum.RISK_FLOW_MENU.getCode());
            String processInstanceId = processServiceApi.startProcessByDefId(workRiskVo.getDefinitionId(), BeanUtil.beanToMap(workRiskVo, new HashMap<>(16), false, false));
            WfBusinessProcess wfBusinessProcess = new WfBusinessProcess();
            wfBusinessProcess.setProcessId(processInstanceId);
            wfBusinessProcess.setBusinessId(String.valueOf(workRiskVo.getRiskId()));
            wfBusinessProcess.setBusinessProcessType(FlowMenuEnum.RISK_FLOW_MENU.getCode());
            wfBusinessProcessServiceApi.insertWfBusinessProcess(wfBusinessProcess);
            workRiskVo.setProcessId(processInstanceId);
            workRiskVo.setSchedule(ProcessStatus.RUNNING.getStatus());
        }
        return workRiskMapper.updateWorkRisk(workRiskVo);
    }

    /**
     * 批量删除隐患上报
     *
     * @param riskIds 需要删除的隐患上报主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteWorkRiskByRiskIds(Long[] riskIds) {
        for (Long riskId : riskIds) {
            WorkRiskVo workRiskVo = checkExistence(riskId);
            if (ProcessStatus.UNAPPROVED.getStatus().equals(workRiskVo.getSchedule()) || ProcessStatus.CANCELED.getStatus().equals(workRiskVo.getSchedule())) {
                wfBusinessProcessServiceApi.deleteWfBusinessProcessByBusinessId(String.valueOf(riskId), FlowMenuEnum.RISK_FLOW_MENU.getCode());
//                WfTaskBo wfTaskBo = new WfTaskBo();
//                wfTaskBo.setProcInsId(workRiskVo.getProcessId());
//                flowTaskServiceApi.stopProcess(wfTaskBo);
                processServiceApi.deleteProcessByIds(new String[]{workRiskVo.getProcessId()});
            }
        }
        return workRiskMapper.deleteWorkRiskByRiskIds(riskIds);
    }

    /**
     * 校验隐患
     *
     * @param riskId
     * @return
     */
    private WorkRiskVo checkExistence(Long riskId) {
        WorkRiskVo workRiskVo = workRiskMapper.selectWorkRiskByRiskId(riskId);
        if (DelFlagEnum.DELETE.getCode().equals(workRiskVo.getDelFlag())) {
            throw new DataException("该[[" + workRiskVo.getUserName() + "]]隐患已被删除");
        }
        return workRiskVo;
    }

    /**
     * 删除隐患上报信息
     *
     * @param riskId 隐患上报主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteWorkRiskByRiskId(Long riskId) {
        WorkRiskVo workRiskVo = checkExistence(riskId);
        if (ProcessStatus.UNAPPROVED.getStatus().equals(workRiskVo.getSchedule()) || ProcessStatus.CANCELED.getStatus().equals(workRiskVo.getSchedule())) {
            wfBusinessProcessServiceApi.deleteWfBusinessProcessByBusinessId(String.valueOf(riskId), FlowMenuEnum.RISK_FLOW_MENU.getCode());
//            WfTaskBo wfTaskBo = new WfTaskBo();
//            wfTaskBo.setProcInsId(workRiskVo.getProcessId());
//            flowTaskServiceApi.stopProcess(wfTaskBo);
            processServiceApi.deleteProcessByIds(new String[]{workRiskVo.getProcessId()});
        }
        return workRiskMapper.deleteWorkRiskByRiskId(riskId);
    }

    /**
     * 提交审核
     *
     * @param riskId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int submit(Long riskId) {
        WorkRiskVo workRiskVo = checkExistence(riskId);

        WorkRiskVo workRisk = new WorkRiskVo();

        // 启动流程
        workRiskVo.setBusinessId(String.valueOf(riskId));
        workRiskVo.setBusinessProcessType(FlowMenuEnum.RISK_FLOW_MENU.getCode());
        String processInstanceId = processServiceApi.startProcessByDefId(workRiskVo.getDefinitionId(), BeanUtil.beanToMap(workRiskVo, new HashMap<>(16), false, false));
        WfBusinessProcess wfBusinessProcess = new WfBusinessProcess();
        wfBusinessProcess.setProcessId(processInstanceId);
        wfBusinessProcess.setBusinessId(String.valueOf(riskId));
        wfBusinessProcess.setBusinessProcessType(FlowMenuEnum.RISK_FLOW_MENU.getCode());
        wfBusinessProcessServiceApi.insertWfBusinessProcess(wfBusinessProcess);

        workRisk.setProcessId(processInstanceId);
        workRisk.setRiskId(riskId);
        workRisk.setSchedule(ProcessStatus.RUNNING.getStatus());
        return workRiskMapper.updateWorkRisk(workRisk);
    }

    /**
     * 撤销审核
     *
     * @param riskId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int revoke(Long riskId) {
        wfBusinessProcessServiceApi.deleteWfBusinessProcessByBusinessId(String.valueOf(riskId), FlowMenuEnum.RISK_FLOW_MENU.getCode());
        WorkRiskVo workRiskVo = checkExistence(riskId);
        WorkRiskVo workRisk = new WorkRiskVo();


        WfTaskBo wfTaskBo = new WfTaskBo();
        wfTaskBo.setProcInsId(workRiskVo.getProcessId());
        flowTaskServiceApi.stopProcess(wfTaskBo);
        processServiceApi.deleteProcessByIds(new String[]{workRiskVo.getProcessId()});

        workRisk.setProcessId("");
        workRisk.setRiskId(riskId);
        workRisk.setSchedule(ProcessStatus.UNAPPROVED.getStatus());
        return workRiskMapper.updateWorkRisk(workRisk);
    }

    /**
     * 重新申请
     *
     * @param riskId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int reapply(Long riskId) {
        WorkRiskVo workRiskVo = checkExistence(riskId);
        processServiceApi.deleteProcessByIds(new String[]{workRiskVo.getProcessId()});
        return submit(riskId);
    }
}
