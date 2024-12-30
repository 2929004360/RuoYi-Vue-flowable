package com.ruoyi.flowable.api.service;

import com.ruoyi.flowable.api.domain.vo.WorkLeaveVo;

/**
 * 请假api接口类
 *
 * @author fengcheng
 */
public interface IWorkLeaveServiceApi {

    /**
     * 根据业务id查询请假单信息
     *
     * @param businessId
     * @return
     */
    WorkLeaveVo selectWorkLeaveByLeaveId(String businessId);

    /**
     * 更新请假单信息
     *
     * @param workLeaveVo
     */
    void updateWorkLeave(WorkLeaveVo workLeaveVo);
}
