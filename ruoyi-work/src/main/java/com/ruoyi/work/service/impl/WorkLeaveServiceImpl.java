package com.ruoyi.work.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.enums.FlowMenuEnum;
import com.ruoyi.common.enums.ProcessStatus;
import com.ruoyi.common.exception.DataException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.flowable.api.domain.WfBusinessProcess;
import com.ruoyi.flowable.api.domain.bo.WfTaskBo;
import com.ruoyi.flowable.api.service.IWfBusinessProcessServiceApi;
import com.ruoyi.flowable.api.service.IWfProcessServiceApi;
import com.ruoyi.flowable.api.service.IWfTaskServiceApi;
import com.ruoyi.flowable.api.domain.vo.WorkLeaveVo;
import com.ruoyi.work.mapper.WorkLeaveMapper;
import com.ruoyi.work.service.IWorkLeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

/**
 * 请假管理Service业务层处理
 *
 * @author fengcheng
 * @date 2024-12-30
 */
@Service
public class WorkLeaveServiceImpl implements IWorkLeaveService {
    @Autowired
    private WorkLeaveMapper workLeaveMapper;

    @Autowired
    private IWfBusinessProcessServiceApi wfBusinessProcessServiceApi;

    @Autowired
    private IWfProcessServiceApi processServiceApi;

    @Autowired
    private IWfTaskServiceApi flowTaskServiceApi;

    /**
     * 查询请假管理
     *
     * @param leaveId 请假管理主键
     * @return 请假管理
     */
    @Override
    public WorkLeaveVo selectWorkLeaveByLeaveId(Long leaveId) {
        return checkExistence(leaveId);
    }

    /**
     * 查询请假管理列表
     *
     * @param workLeaveVo 请假管理
     * @return 请假管理
     */
    @Override
    public List<WorkLeaveVo> selectWorkLeaveList(WorkLeaveVo workLeaveVo) {
        return workLeaveMapper.selectWorkLeaveList(workLeaveVo);
    }

    /**
     * 新增请假管理
     *
     * @param workLeaveVo 请假管理
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertWorkLeave(WorkLeaveVo workLeaveVo) {
        workLeaveVo.setCreateTime(DateUtils.getNowDate());
        int i = workLeaveMapper.insertWorkLeave(workLeaveVo);

        workLeaveVo.setBusinessId(String.valueOf(workLeaveVo.getLeaveId()));
        workLeaveVo.setBusinessProcessType(FlowMenuEnum.LEAVE_FLOW_MENU.getCode());

        if (ProcessStatus.RUNNING.getStatus().equals(workLeaveVo.getSchedule())) {
            String processInstanceId = processServiceApi.startProcessByDefId(workLeaveVo.getDefinitionId(), BeanUtil.beanToMap(workLeaveVo, new HashMap<>(16), false, false));
            WfBusinessProcess wfBusinessProcess = new WfBusinessProcess();
            wfBusinessProcess.setProcessId(processInstanceId);
            wfBusinessProcess.setBusinessId(String.valueOf(workLeaveVo.getLeaveId()));
            wfBusinessProcess.setBusinessProcessType(FlowMenuEnum.LEAVE_FLOW_MENU.getCode());
            wfBusinessProcessServiceApi.insertWfBusinessProcess(wfBusinessProcess);
            workLeaveVo.setProcessId(processInstanceId);
            workLeaveVo.setSchedule(ProcessStatus.RUNNING.getStatus());
        }
        return i;
    }

    /**
     * 修改请假管理
     *
     * @param workLeaveVo 请假管理
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateWorkLeave(WorkLeaveVo workLeaveVo) {
        checkExistence(workLeaveVo.getLeaveId());
        workLeaveVo.setUpdateTime(DateUtils.getNowDate());

        if (ProcessStatus.RUNNING.getStatus().equals(workLeaveVo.getSchedule())) {
            workLeaveVo.setBusinessId(String.valueOf(workLeaveVo.getLeaveId()));
            workLeaveVo.setBusinessProcessType(FlowMenuEnum.LEAVE_FLOW_MENU.getCode());
            String processInstanceId = processServiceApi.startProcessByDefId(workLeaveVo.getDefinitionId(), BeanUtil.beanToMap(workLeaveVo, new HashMap<>(16), false, false));
            WfBusinessProcess wfBusinessProcess = new WfBusinessProcess();
            wfBusinessProcess.setProcessId(processInstanceId);
            wfBusinessProcess.setBusinessId(String.valueOf(workLeaveVo.getLeaveId()));
            wfBusinessProcess.setBusinessProcessType(FlowMenuEnum.LEAVE_FLOW_MENU.getCode());
            wfBusinessProcessServiceApi.insertWfBusinessProcess(wfBusinessProcess);
            workLeaveVo.setProcessId(processInstanceId);
            workLeaveVo.setSchedule(ProcessStatus.RUNNING.getStatus());
        }
        return workLeaveMapper.updateWorkLeave(workLeaveVo);
    }

    /**
     * 批量删除请假管理
     *
     * @param leaveIds 需要删除的请假管理主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteWorkLeaveByLeaveIds(Long[] leaveIds) {
        for (Long leaveId : leaveIds) {
            wfBusinessProcessServiceApi.deleteWfBusinessProcessByBusinessId(String.valueOf(leaveId), FlowMenuEnum.LEAVE_FLOW_MENU.getCode());
            WorkLeaveVo workLeaveVo = checkExistence(leaveId);

            if (ProcessStatus.UNAPPROVED.getStatus().equals(workLeaveVo.getSchedule()) || ProcessStatus.CANCELED.getStatus().equals(workLeaveVo.getSchedule())) {
//                WfTaskBo wfTaskBo = new WfTaskBo();
//                wfTaskBo.setProcInsId(workLeaveVo.getProcessId());
//                flowTaskServiceApi.stopProcess(wfTaskBo);
                processServiceApi.deleteProcessByIds(new String[]{workLeaveVo.getProcessId()});
            }
        }
        return workLeaveMapper.deleteWorkLeaveByLeaveIds(leaveIds);
    }

    /**
     * 删除请假管理信息
     *
     * @param leaveId 请假管理主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteWorkLeaveByLeaveId(Long leaveId) {
        wfBusinessProcessServiceApi.deleteWfBusinessProcessByBusinessId(String.valueOf(leaveId), FlowMenuEnum.LEAVE_FLOW_MENU.getCode());
        WorkLeaveVo workLeaveVo = checkExistence(leaveId);

        if (ProcessStatus.UNAPPROVED.getStatus().equals(workLeaveVo.getSchedule()) || ProcessStatus.CANCELED.getStatus().equals(workLeaveVo.getSchedule())) {
//            WfTaskBo wfTaskBo = new WfTaskBo();
//            wfTaskBo.setProcInsId(workLeaveVo.getProcessId());
//            flowTaskServiceApi.stopProcess(wfTaskBo);
            processServiceApi.deleteProcessByIds(new String[]{workLeaveVo.getProcessId()});
        }
        return workLeaveMapper.deleteWorkLeaveByLeaveId(leaveId);
    }

    /**
     * 重新申请
     *
     * @param leaveId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int reapply(Long leaveId) {
        WorkLeaveVo workLeaveVo = checkExistence(leaveId);
        processServiceApi.deleteProcessByIds(new String[]{workLeaveVo.getProcessId()});
        return submit(leaveId);
    }

    /**
     * 撤销审核
     *
     * @param leaveId
     * @return
     */
    @Override
    public int revoke(Long leaveId) {
        wfBusinessProcessServiceApi.deleteWfBusinessProcessByBusinessId(String.valueOf(leaveId), FlowMenuEnum.LEAVE_FLOW_MENU.getCode());
        WorkLeaveVo workLeaveVo = checkExistence(leaveId);
        WorkLeaveVo workLeave = new WorkLeaveVo();


        WfTaskBo wfTaskBo = new WfTaskBo();
        wfTaskBo.setProcInsId(workLeaveVo.getProcessId());
        flowTaskServiceApi.stopProcess(wfTaskBo);
        processServiceApi.deleteProcessByIds(new String[]{workLeaveVo.getProcessId()});

        workLeave.setProcessId("");
        workLeave.setLeaveId(leaveId);
        workLeave.setSchedule(ProcessStatus.UNAPPROVED.getStatus());
        return workLeaveMapper.updateWorkLeave(workLeave);
    }

    /**
     * 提交审核
     *
     * @param leaveId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int submit(Long leaveId) {
        WorkLeaveVo workLeaveVo = checkExistence(leaveId);

        WorkLeaveVo workLeave = new WorkLeaveVo();

        // 启动流程
        workLeaveVo.setBusinessId(String.valueOf(leaveId));
        workLeaveVo.setBusinessProcessType(FlowMenuEnum.LEAVE_FLOW_MENU.getCode());
        String processInstanceId = processServiceApi.startProcessByDefId(workLeaveVo.getDefinitionId(), BeanUtil.beanToMap(workLeaveVo, new HashMap<>(16), false, false));
        WfBusinessProcess wfBusinessProcess = new WfBusinessProcess();
        wfBusinessProcess.setProcessId(processInstanceId);
        wfBusinessProcess.setBusinessId(String.valueOf(leaveId));
        wfBusinessProcess.setBusinessProcessType(FlowMenuEnum.LEAVE_FLOW_MENU.getCode());
        wfBusinessProcessServiceApi.insertWfBusinessProcess(wfBusinessProcess);

        workLeave.setProcessId(processInstanceId);
        workLeave.setLeaveId(leaveId);
        workLeave.setSchedule(ProcessStatus.RUNNING.getStatus());
        return workLeaveMapper.updateWorkLeave(workLeave);
    }

    /**
     * 校验请假
     *
     * @param leaveId
     * @return
     */
    private WorkLeaveVo checkExistence(Long leaveId) {
        WorkLeaveVo workLeaveVo = workLeaveMapper.selectWorkLeaveByLeaveId(leaveId);
        if (DelFlagEnum.DELETE.getCode().equals(workLeaveVo.getDelFlag())) {
            throw new DataException("该[[" + workLeaveVo.getUserName() + "]]请假已被删除");
        }
        return workLeaveVo;
    }
}
