package com.ruoyi.flowable.api.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 隐患上报对象 work_risk
 *
 * @author fengcheng
 * @date 2025-03-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkRisk extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 隐患ID
     */
    private Long riskId;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 厂区ID
     */
    private Long factoryId;

    /**
     * 部门名称
     */
    @Excel(name = "部门名称")
    private String deptName;

    /**
     * 申请人
     */
    @Excel(name = "申请人")
    private String userName;

    /**
     * 厂区名称
     */
    @Excel(name = "厂区名称")
    private String factoryName;

    /**
     * 流程定义ID
     */
    private String definitionId;

    /**
     * 流程ID
     */
    private String processId;

    /**
     * 流程名称
     */
    private String processName;

    /**
     * 隐患区域(1=炼铁,2=炼钢,3=轧钢,4=能源,5=机加,6=原料,7=物流,8=计量)
     */
    @Excel(name = "隐患区域", dictType = "work_risk_area")
    private String riskArea;

    /**
     * 隐患类别(1=设备老化,2=违章作业,3=杂物堆积,4=其他)
     */
    @Excel(name = "隐患类别", dictType = "work_risk_type")
    private String riskType;

    /**
     * 隐患级别(1=高,2=中,3=低,4=其他)
     */
    @Excel(name = "隐患级别", dictType = "work_risk_level")
    private String riskLevel;

    /**
     * 防范措施(1=措施一,2=措施二,3=措施三,4=其他)
     */
    @Excel(name = "防范措施", dictType = "work_risk_measure")
    private String measure;

    /**
     * 完成时间
     */
    @Excel(name = "完成时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date completionTime;

    /**
     * 上传图片
     */
    @Excel(name = "上传图片", cellType = Excel.ColumnType.IMAGE, height = 50)
    private String image;

    /**
     * 整改照片
     */
    @Excel(name = "整改照片", cellType = Excel.ColumnType.IMAGE, height = 50)
    private String photo;

    /**
     * 审批进度(unapproved=未审批,running=进行中,terminated=已终止,completed=已完成,canceled=已取消)
     */
    @Excel(name = "审批进度", dictType = "common_schedule")
    private String schedule;

    /**
     * 删除标志（0代表存在,1代表删除）
     */
    private String delFlag;

    /**
     * 备注
     */
    @Excel(name = "备注")
    private String remark;
}
