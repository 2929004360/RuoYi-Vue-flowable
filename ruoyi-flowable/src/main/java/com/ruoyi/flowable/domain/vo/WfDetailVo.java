package com.ruoyi.flowable.domain.vo;

import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.flowable.core.FormConf;
import com.ruoyi.flowable.domain.WfRoamHistorical;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 流程详情视图对象
 *
 * @author fengcheng
 * @createTime 2022/8/7 15:01
 */
@Data
public class WfDetailVo {

    /**
     * 任务表单信息
     */
    private FormConf taskFormData;

    /**
     * 历史流程节点信息
     */
    private List<WfProcNodeVo> historyProcNodeList;

    /**
     * 历史流程审批节点信息
     */
    private List<WfRoamHistorical> historyApproveProcNodeList;

    /**
     * 流程表单列表
     */
    private List<FormConf> processFormList;

    /**
     * 显示按钮组
     */
    private List<String> buttonList;

    /**
     * 流程XML
     */
    private String bpmnXml;

    /**
     * 任务追踪视图
     */
    private WfViewerVo flowViewer;

    /**
     * 是否存在任务表单信息
     *
     * @return true:存在；false:不存在
     */
    public Boolean isExistTaskForm() {
        return ObjectUtil.isNotEmpty(this.taskFormData);
    }

    /**
     * 业务流程
     */
    private Map<String, Object> businessProcess;
}
