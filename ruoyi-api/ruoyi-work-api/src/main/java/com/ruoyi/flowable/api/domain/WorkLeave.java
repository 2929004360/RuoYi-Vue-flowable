package com.ruoyi.flowable.api.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 请假管理对象 work_leave
 *
 * @author fengcheng
 * @date 2024-12-30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkLeave extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 请假ID
     */
    private Long leaveId;

    /**
     * 用户ID
     */
    @Excel(name = "用户ID")
    private Long userId;

    /**
     * 申请人
     */
    @Excel(name = "申请人")
    private String userName;

    /**
     * 流程定义ID
     */
    @Excel(name = "流程定义ID")
    private String definitionId;

    /**
     * 流程ID
     */
    @Excel(name = "流程ID")
    private String processId;

    /**
     * 流程名称
     */
    @Excel(name = "流程名称")
    private String processName;

    /**
     * 请假类别(1=病假,2=调休假,3=法定假,4,探亲假,5=年休假,6=事假,7=其他)
     */
    @Excel(name = "请假类别(1=病假,2=调休假,3=法定假,4,探亲假,5=年休假,6=事假,7=其他)")
    private String category;

    /**
     * 请假天数
     */
    @Excel(name = "请假天数")
    private BigDecimal holiday;

    /**
     * 职位
     */
    @Excel(name = "职位")
    private String position;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "开始时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startTime;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "结束时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endTime;

    /**
     * 请假内容
     */
    @Excel(name = "请假内容")
    private String content;

    /**
     * 是否补假(0=否,1=是)
     */
    @Excel(name = "是否补假(0=否,1=是)")
    private String isRepair;

    /**
     * 审批进度(unapproved=未审批,running=进行中,terminated=已终止,completed=已完成,canceled=已取消)
     */
    @Excel(name = "审批进度(unapproved=未审批,running=进行中,terminated=已终止,completed=已完成,canceled=已取消)")
    private String schedule;

    /**
     * 删除标志（0代表存在,1代表删除）
     */
    private String delFlag;
}
