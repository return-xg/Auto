package com.auto.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.auto.common.annotation.Excel;
import com.auto.common.core.domain.BaseEntity;

/**
 * 预约安装对象 appointment
 *
 * @author auto
 * @date 2026-01-07
 */
public class Appointment extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 订单ID */
    @Excel(name = "订单ID")
    private Long orderId;

    /** 门店ID */
    @Excel(name = "门店ID")
    private Long storeId;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 预约安装时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "预约安装时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date appointmentTime;

    /** 状态（0待确认，1已确认，2已完成，3已取消） */
    @Excel(name = "状态", readConverterExp = "0=待确认，1已确认，2已完成，3已取消")
    private Long status;

    /** 备注 */
    @Excel(name = "备注")
    private String note;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }

    public void setOrderId(Long orderId)
    {
        this.orderId = orderId;
    }

    public Long getOrderId()
    {
        return orderId;
    }

    public void setStoreId(Long storeId)
    {
        this.storeId = storeId;
    }

    public Long getStoreId()
    {
        return storeId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setAppointmentTime(Date appointmentTime)
    {
        this.appointmentTime = appointmentTime;
    }

    public Date getAppointmentTime()
    {
        return appointmentTime;
    }

    public void setStatus(Long status)
    {
        this.status = status;
    }

    public Long getStatus()
    {
        return status;
    }

    public void setNote(String note)
    {
        this.note = note;
    }

    public String getNote()
    {
        return note;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("orderId", getOrderId())
                .append("storeId", getStoreId())
                .append("userId", getUserId())
                .append("appointmentTime", getAppointmentTime())
                .append("status", getStatus())
                .append("note", getNote())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
