package com.ruoyi.flowable.service;

import com.ruoyi.flowable.api.domain.WfBusinessProcess;

import java.util.List;

/**
 * 业务流程Service接口
 *
 * @author fengcheng
 * @date 2024-07-15
 */
public interface IWfBusinessProcessService {
    /**
     * 查询业务流程
     *
     * @param businessId 业务流程主键
     * @return 业务流程
     */
    public WfBusinessProcess selectWfBusinessProcessByBusinessId(String businessId);

    /**
     * 查询业务流程列表
     *
     * @param wfBusinessProcess 业务流程
     * @return 业务流程集合
     */
    public List<WfBusinessProcess> selectWfBusinessProcessList(WfBusinessProcess wfBusinessProcess);

    /**
     * 新增业务流程
     *
     * @param wfBusinessProcess 业务流程
     * @return 结果
     */
    public int insertWfBusinessProcess(WfBusinessProcess wfBusinessProcess);

    /**
     * 修改业务流程
     *
     * @param wfBusinessProcess 业务流程
     * @return 结果
     */
    public int updateWfBusinessProcess(WfBusinessProcess wfBusinessProcess);

    /**
     * 批量删除业务流程
     *
     * @param businessIds 需要删除的业务流程主键集合
     * @return 结果
     */
    public int deleteWfBusinessProcessByBusinessIds(String[] businessIds);

    /**
     * 删除业务流程信息
     *
     * @param businessId 业务流程主键
     * @param type       业务类型
     * @return 结果
     */
    public int deleteWfBusinessProcessByBusinessId(String businessId, String type);

    /**
     * 根据流程ID查询业务流程
     *
     * @param processId
     * @return
     */
    WfBusinessProcess selectWfBusinessProcessByProcessId(String processId);

    /**
     * 根据流程ID查询业务流程列表
     *
     * @param ids
     * @return
     */
    List<WfBusinessProcess> selectWfBusinessProcessListByProcessId(List<String> ids);
}
