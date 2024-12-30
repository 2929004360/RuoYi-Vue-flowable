package com.ruoyi.flowable.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 待办流程对象导出VO
 *
 * @author fengcheng
 */
@Data
@NoArgsConstructor
public class WfTodoTaskExportVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 任务编号
     */
    @Excel(name = "任务编号")
    private String taskId;

    /**
     * 流程名称
     */
    @Excel(name = "流程名称")
    private String procDefName;

    /**
     * 任务节点
     */
    @Excel(name = "任务节点")
    private String taskName;

    /**
     * 流程版本
     */
    @Excel(name = "流程版本")
    private int procDefVersion;

    /**
     * 流程发起人名称
     */
    @Excel(name = "流程发起人")
    private String startUserName;

    /**
     * 接收时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "接收时间")
    private Date createTime;
}
