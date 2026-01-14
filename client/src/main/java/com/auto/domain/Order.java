package com.auto.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.auto.common.annotation.Excel;
import com.auto.common.core.domain.BaseEntity;

/**
 * 订单对象 order
 *
 * @author auto
 * @date 2026-01-07
 */
public class Order extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 订单号 */
    @Excel(name = "订单号")
    private String orderNo;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 订单总金额 */
    @Excel(name = "订单总金额")
    private BigDecimal totalPrice;

    /** 实际支付金额 */
    @Excel(name = "实际支付金额")
    private BigDecimal payPrice;

    /** 运费 */
    @Excel(name = "运费")
    private BigDecimal freightPrice;

    /** 支付方式（1支付宝，2微信…） */
    @Excel(name = "支付方式", readConverterExp = "1=支付宝，2微信…")
    private Integer payType;

    /** 订单状态（0待支付，1待发货，2已发货，3已完成，4已取消） */
    @Excel(name = "订单状态", readConverterExp = "0=待支付，1待发货，2已发货，3已完成，4已取消")
    private Long status;

    /** 配送方式（1送货上门，2门店安装） */
    @Excel(name = "配送方式", readConverterExp = "1=送货上门，2门店安装")
    private Integer deliveryType;

    /** 收货地址ID */
    @Excel(name = "收货地址ID")
    private Long addressId;

    /** 收货地址信息 */
    private UserAddress addressInfo;

    /** 门店ID（如果选择门店安装） */
    @Excel(name = "门店ID", readConverterExp = "如=果选择门店安装")
    private Long storeId;

    /** 预约安装ID */
    @Excel(name = "预约安装ID")
    private Long appointmentId;

    /** 支付交易号 */
    @Excel(name = "支付交易号")
    private String transactionId;

    /** 支付时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "支付时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date payTime;

    /** 发货时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "发货时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date deliveryTime;

    /** 确认收货时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "确认收货时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date receiveTime;

    /** 订单关闭时间（取消/完成） */
    @Excel(name = "订单关闭时间", readConverterExp = "取=消/完成")
    private Date closeTime;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }

    public void setOrderNo(String orderNo)
    {
        this.orderNo = orderNo;
    }

    public String getOrderNo()
    {
        return orderNo;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setTotalPrice(BigDecimal totalPrice)
    {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getTotalPrice()
    {
        return totalPrice;
    }

    public void setPayPrice(BigDecimal payPrice)
    {
        this.payPrice = payPrice;
    }

    public BigDecimal getPayPrice()
    {
        return payPrice;
    }

    public void setFreightPrice(BigDecimal freightPrice)
    {
        this.freightPrice = freightPrice;
    }

    public BigDecimal getFreightPrice()
    {
        return freightPrice;
    }

    public void setPayType(Integer payType)
    {
        this.payType = payType;
    }

    public Integer getPayType()
    {
        return payType;
    }

    public void setStatus(Long status)
    {
        this.status = status;
    }

    public Long getStatus()
    {
        return status;
    }

    public void setDeliveryType(Integer deliveryType)
    {
        this.deliveryType = deliveryType;
    }

    public Integer getDeliveryType()
    {
        return deliveryType;
    }

    public void setAddressId(Long addressId)
    {
        this.addressId = addressId;
    }

    public Long getAddressId()
    {
        return addressId;
    }

    public void setAddressInfo(UserAddress addressInfo)
    {
        this.addressInfo = addressInfo;
    }

    public UserAddress getAddressInfo()
    {
        return addressInfo;
    }

    public void setStoreId(Long storeId)
    {
        this.storeId = storeId;
    }

    public Long getStoreId()
    {
        return storeId;
    }

    public void setAppointmentId(Long appointmentId)
    {
        this.appointmentId = appointmentId;
    }

    public Long getAppointmentId()
    {
        return appointmentId;
    }

    public void setTransactionId(String transactionId)
    {
        this.transactionId = transactionId;
    }

    public String getTransactionId()
    {
        return transactionId;
    }

    public void setPayTime(Date payTime)
    {
        this.payTime = payTime;
    }

    public Date getPayTime()
    {
        return payTime;
    }

    public void setDeliveryTime(Date deliveryTime)
    {
        this.deliveryTime = deliveryTime;
    }

    public Date getDeliveryTime()
    {
        return deliveryTime;
    }

    public void setReceiveTime(Date receiveTime)
    {
        this.receiveTime = receiveTime;
    }

    public Date getReceiveTime()
    {
        return receiveTime;
    }

    public void setCloseTime(Date closeTime)
    {
        this.closeTime = closeTime;
    }

    public Date getCloseTime()
    {
        return closeTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("orderNo", getOrderNo())
                .append("userId", getUserId())
                .append("totalPrice", getTotalPrice())
                .append("payPrice", getPayPrice())
                .append("freightPrice", getFreightPrice())
                .append("payType", getPayType())
                .append("status", getStatus())
                .append("deliveryType", getDeliveryType())
                .append("addressId", getAddressId())
                .append("storeId", getStoreId())
                .append("appointmentId", getAppointmentId())
                .append("transactionId", getTransactionId())
                .append("payTime", getPayTime())
                .append("deliveryTime", getDeliveryTime())
                .append("receiveTime", getReceiveTime())
                .append("closeTime", getCloseTime())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
