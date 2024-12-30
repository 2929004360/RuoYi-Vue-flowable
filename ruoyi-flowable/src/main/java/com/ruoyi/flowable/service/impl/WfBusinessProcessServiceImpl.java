package com.ruoyi.flowable.service.impl;

import com.ruoyi.flowable.api.domain.WfBusinessProcess;
import com.ruoyi.flowable.mapper.WfBusinessProcessMapper;
import com.ruoyi.flowable.service.IWfBusinessProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 业务流程Service业务层处理
 *
 * @author fengcheng
 * @date 2024-07-15
 */
@Service
public class WfBusinessProcessServiceImpl implements IWfBusinessProcessService {
    @Autowired
    private WfBusinessProcessMapper wfBusinessProcessMapper;

    /**
     * 查询业务流程
     *
     * @param businessId 业务流程主键
     * @return 业务流程
     */
    @Override
    public WfBusinessProcess selectWfBusinessProcessByBusinessId(String businessId) {
        return wfBusinessProcessMapper.selectWfBusinessProcessByBusinessId(businessId);
    }

    /**
     * 查询业务流程列表
     *
     * @param wfBusinessProcess 业务流程
     * @return 业务流程
     */
    @Override
    public List<WfBusinessProcess> selectWfBusinessProcessList(WfBusinessProcess wfBusinessProcess) {
        return wfBusinessProcessMapper.selectWfBusinessProcessList(wfBusinessProcess);
    }

    /**
     * 新增业务流程
     *
     * @param wfBusinessProcess 业务流程
     * @return 结果
     */
    @Override
    public int insertWfBusinessProcess(WfBusinessProcess wfBusinessProcess) {
        return wfBusinessProcessMapper.insertWfBusinessProcess(wfBusinessProcess);
    }

    /**
     * 修改业务流程
     *
     * @param wfBusinessProcess 业务流程
     * @return 结果
     */
    @Override
    public int updateWfBusinessProcess(WfBusinessProcess wfBusinessProcess) {
        return wfBusinessProcessMapper.updateWfBusinessProcess(wfBusinessProcess);
    }

    /**
     * 批量删除业务流程
     *
     * @param businessIds 需要删除的业务流程主键
     * @return 结果
     */
    @Override
    public int deleteWfBusinessProcessByBusinessIds(String[] businessIds) {
        return wfBusinessProcessMapper.deleteWfBusinessProcessByBusinessIds(businessIds);
    }

    /**
     * 删除业务流程信息
     *
     * @param businessId 业务流程主键
     * @param type       业务类型
     * @return 结果
     */
    @Override
    public int deleteWfBusinessProcessByBusinessId(String businessId, String type) {
        return wfBusinessProcessMapper.deleteWfBusinessProcessByBusinessId(businessId, type);
    }

    /**
     * 根据流程ID查询业务流程
     *
     * @param processId
     * @return
     */
    @Override
    public WfBusinessProcess selectWfBusinessProcessByProcessId(String processId) {
        return wfBusinessProcessMapper.selectWfBusinessProcessByProcessId(processId);
    }

    /**
     * 根据流程ID查询业务流程列表
     *
     * @param ids
     * @return
     */
    @Override
    public List<WfBusinessProcess> selectWfBusinessProcessListByProcessId(List<String> ids) {
        return wfBusinessProcessMapper.selectWfBusinessProcessListByProcessId(ids);
    }
}
