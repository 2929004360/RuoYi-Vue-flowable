package com.ruoyi.work.service;

import java.util.List;
import com.ruoyi.flowable.api.domain.vo.WorkLeaveVo;

/**
 * 请假管理Service接口
 *
 * @author fengcheng
 * @date 2024-12-30
 */
public interface IWorkLeaveService
{
    /**
     * 查询请假管理
     *
     * @param leaveId 请假管理主键
     * @return 请假管理
     */
    public WorkLeaveVo selectWorkLeaveByLeaveId(Long leaveId);

    /**
     * 查询请假管理列表
     *
     * @param workLeaveVo 请假管理
     * @return 请假管理集合
     */
    public List<WorkLeaveVo> selectWorkLeaveList(WorkLeaveVo workLeaveVo);

    /**
     * 新增请假管理
     *
     * @param workLeaveVo 请假管理
     * @return 结果
     */
    public int insertWorkLeave(WorkLeaveVo workLeaveVo);

    /**
     * 修改请假管理
     *
     * @param workLeaveVo 请假管理
     * @return 结果
     */
    public int updateWorkLeave(WorkLeaveVo workLeaveVo);

    /**
     * 批量删除请假管理
     *
     * @param leaveIds 需要删除的请假管理主键集合
     * @return 结果
     */
    public int deleteWorkLeaveByLeaveIds(Long[] leaveIds);

    /**
     * 删除请假管理信息
     *
     * @param leaveId 请假管理主键
     * @return 结果
     */
    public int deleteWorkLeaveByLeaveId(Long leaveId);

    /**
     * 重新申请
     *
     * @param leaveId
     * @return
     */
    int reapply(Long leaveId);

    /**
     * 撤销审核
     *
     * @param leaveId
     * @return
     */
    int revoke(Long leaveId);

    /**
     * 提交审核
     *
     * @param leaveId
     * @return
     */
    int submit(Long leaveId);
}
