package com.ruoyi.flowable.handler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.enums.FlowMenuEnum;
import com.ruoyi.flowable.api.domain.bo.WfTaskBo;
import com.ruoyi.flowable.api.domain.vo.WorkRiskVo;
import com.ruoyi.flowable.api.service.IWorkRiskServiceApi;
import com.ruoyi.flowable.constant.ProcessConstants;
import org.flowable.engine.history.HistoricProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 业务表单数据类
 *
 * @author fengcheng
 */
@Component("businessFormDataHandler")
public class BusinessFormDataHandler {

    @Autowired
    @Lazy
    private IWorkRiskServiceApi workRiskServiceApi;


    /**
     * 设置业务表单数据
     *
     * @param taskBo
     * @param historicProcIns
     */
    public void setDataHandle(WfTaskBo taskBo, HistoricProcessInstance historicProcIns) {
        Map<String, Object> processVariables = historicProcIns.getProcessVariables();
        Object businessProcess = processVariables.get(ProcessConstants.BUSINESS_PROCESS_TYPE);
        if(ObjectUtil.isNull(businessProcess)){
            return;
        }
        String businessProcessType = businessProcess.toString();
        // 隐患流程
        if (FlowMenuEnum.RISK_FLOW_MENU.getCode().equals(businessProcessType)) {
            WorkRiskVo workRisk = BeanUtil.copyProperties(processVariables, WorkRiskVo.class);
            WorkRiskVo workRiskVo = BeanUtil.copyProperties(taskBo.getVariables(), WorkRiskVo.class);

            if (ObjectUtil.isNull(workRiskVo)) {
                return;
            }
            WorkRiskVo workRiskUpdate = new WorkRiskVo();
            workRiskUpdate.setRiskId(workRisk.getRiskId());
            workRiskUpdate.setRiskLevel(workRiskVo.getRiskLevel());
            workRiskUpdate.setMeasure(workRiskVo.getMeasure());

            if (ObjectUtil.isNotNull(taskBo.getVariables().get("photo"))) {
                ArrayList<LinkedHashMap<String, Object>> photoList = (ArrayList<LinkedHashMap<String, Object>>) taskBo.getVariables().get("photo");
                ArrayList<String> list = new ArrayList<>();
                for (LinkedHashMap<String, Object> map : photoList) {
                    list.add(map.get("fileName").toString());
                }
                workRiskUpdate.setPhoto(StrUtil.join(",", list));
            }

            workRiskServiceApi.updateWorkRisk(workRiskUpdate);
        }
    }
}
