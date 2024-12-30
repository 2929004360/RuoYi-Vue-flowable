package com.ruoyi.work.mapper;

import java.util.List;

import com.ruoyi.flowable.api.domain.WorkLeave;
import com.ruoyi.flowable.api.domain.vo.WorkLeaveVo;

/**
 * 请假管理Mapper接口
 *
 * @author fengcheng
 * @date 2024-12-30
 */
public interface WorkLeaveMapper
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
     * @param workLeave 请假管理
     * @return 结果
     */
    public int insertWorkLeave(WorkLeave workLeave);

    /**
     * 修改请假管理
     *
     * @param workLeave 请假管理
     * @return 结果
     */
    public int updateWorkLeave(WorkLeave workLeave);

    /**
     * 删除请假管理
     *
     * @param leaveId 请假管理主键
     * @return 结果
     */
    public int deleteWorkLeaveByLeaveId(Long leaveId);

    /**
     * 批量删除请假管理
     *
     * @param leaveIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteWorkLeaveByLeaveIds(Long[] leaveIds);
}
