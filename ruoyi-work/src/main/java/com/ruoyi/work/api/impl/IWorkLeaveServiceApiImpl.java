package com.ruoyi.work.api.impl;

import com.ruoyi.flowable.api.domain.vo.WorkLeaveVo;
import com.ruoyi.flowable.api.service.IWorkLeaveServiceApi;
import com.ruoyi.work.mapper.WorkLeaveMapper;
import com.ruoyi.work.service.IWorkLeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 请假api接口实现类
 *
 * @author fengcheng
 */
@RequiredArgsConstructor
@Service
public class IWorkLeaveServiceApiImpl implements IWorkLeaveServiceApi {

    private final WorkLeaveMapper workLeaveMapper;
    ;

    /**
     * 根据业务id查询请假单信息
     *
     * @param businessId
     * @return
     */
    @Override
    public WorkLeaveVo selectWorkLeaveByLeaveId(String businessId) {
        return workLeaveMapper.selectWorkLeaveByLeaveId(Long.valueOf(businessId));
    }

    /**
     * 更新请假单信息
     *
     * @param workLeaveVo
     */
    @Override
    public void updateWorkLeave(WorkLeaveVo workLeaveVo) {
        workLeaveMapper.updateWorkLeave(workLeaveVo);
    }
}
