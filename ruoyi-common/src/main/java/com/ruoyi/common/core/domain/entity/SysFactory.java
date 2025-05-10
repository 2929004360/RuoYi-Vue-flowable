package com.ruoyi.common.core.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 厂区对象 sys_factory
 *
 * @author ruoyi
 * @date 2025-03-15
 */
public class SysFactory extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 厂区ID */
    private Long factoryId;

    /** 厂区名称 */
    @Excel(name = "厂区名称")
    private String factoryName;

    /** 厂区位置 */
    @Excel(name = "厂区位置")
    private String location;

    /** 厂区面积 */
    @Excel(name = "厂区面积")
    private BigDecimal areaSize;

    /** 建立时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "建立时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date establishedTime;

    /** 联系电话 */
    @Excel(name = "联系电话")
    private String contactNumber;

    /** 厂区经理姓名 */
    @Excel(name = "厂区经理姓名")
    private String managerName;

    /** 员工数量 */
    @Excel(name = "员工数量")
    private Long numberOfEmployees;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    public void setFactoryId(Long factoryId)
    {
        this.factoryId = factoryId;
    }

    public Long getFactoryId()
    {
        return factoryId;
    }
    public void setFactoryName(String factoryName)
    {
        this.factoryName = factoryName;
    }

    public String getFactoryName()
    {
        return factoryName;
    }
    public void setLocation(String location)
    {
        this.location = location;
    }

    public String getLocation()
    {
        return location;
    }
    public void setAreaSize(BigDecimal areaSize)
    {
        this.areaSize = areaSize;
    }

    public BigDecimal getAreaSize()
    {
        return areaSize;
    }
    public void setEstablishedTime(Date establishedTime)
    {
        this.establishedTime = establishedTime;
    }

    public Date getEstablishedTime()
    {
        return establishedTime;
    }
    public void setContactNumber(String contactNumber)
    {
        this.contactNumber = contactNumber;
    }

    public String getContactNumber()
    {
        return contactNumber;
    }
    public void setManagerName(String managerName)
    {
        this.managerName = managerName;
    }

    public String getManagerName()
    {
        return managerName;
    }
    public void setNumberOfEmployees(Long numberOfEmployees)
    {
        this.numberOfEmployees = numberOfEmployees;
    }

    public Long getNumberOfEmployees()
    {
        return numberOfEmployees;
    }
    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("factoryId", getFactoryId())
            .append("factoryName", getFactoryName())
            .append("location", getLocation())
            .append("areaSize", getAreaSize())
            .append("establishedTime", getEstablishedTime())
            .append("contactNumber", getContactNumber())
            .append("managerName", getManagerName())
            .append("numberOfEmployees", getNumberOfEmployees())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
