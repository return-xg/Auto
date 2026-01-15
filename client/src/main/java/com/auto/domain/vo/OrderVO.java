package com.auto.domain.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import com.auto.domain.UserAddress;

public class OrderVO {
    private Long id;
    private String orderNo;
    private Long userId;
    private String userName;
    private BigDecimal totalPrice;
    private BigDecimal payPrice;
    private BigDecimal freightPrice;
    private Integer payType;
    private Long status;
    private String statusText;
    private Integer deliveryType;
    private String deliveryTypeText;
    private Long addressId;
    private UserAddress addressInfo;
    private Long storeId;
    private String storeInfo;
    private Long appointmentId;
    private String transactionId;
    private Date payTime;
    private Date deliveryTime;
    private Date receiveTime;
    private Date closeTime;
    private Date createTime;
    private Date updateTime;
    private List<OrderProductVO> products;
    private String logisticsInfo;
    private Long refundStatus;
    private Boolean canCancel;
    private Boolean canPay;
    private Boolean canConfirm;
    private Boolean canRefund;
    private String beginTime;
    private String endTime;
    private Long refundId;
    private Long refundOrderId;
    private Long refundUserId;
    private Integer refundType;
    private String refundReason;
    private BigDecimal refundAmount;
    private String refundEvidence;
    private String refundAdminRemark;
    private Date refundCreateTime;
    private Date refundUpdateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getPayPrice() {
        return payPrice;
    }

    public void setPayPrice(BigDecimal payPrice) {
        this.payPrice = payPrice;
    }

    public BigDecimal getFreightPrice() {
        return freightPrice;
    }

    public void setFreightPrice(BigDecimal freightPrice) {
        this.freightPrice = freightPrice;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public Integer getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(Integer deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getDeliveryTypeText() {
        return deliveryTypeText;
    }

    public void setDeliveryTypeText(String deliveryTypeText) {
        this.deliveryTypeText = deliveryTypeText;
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public UserAddress getAddressInfo() {
        return addressInfo;
    }

    public void setAddressInfo(UserAddress addressInfo) {
        this.addressInfo = addressInfo;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getStoreInfo() {
        return storeInfo;
    }

    public void setStoreInfo(String storeInfo) {
        this.storeInfo = storeInfo;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public Date getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Date deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public Date getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(Date receiveTime) {
        this.receiveTime = receiveTime;
    }

    public Date getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(Date closeTime) {
        this.closeTime = closeTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public List<OrderProductVO> getProducts() {
        return products;
    }

    public void setProducts(List<OrderProductVO> products) {
        this.products = products;
    }

    public String getLogisticsInfo() {
        return logisticsInfo;
    }

    public void setLogisticsInfo(String logisticsInfo) {
        this.logisticsInfo = logisticsInfo;
    }

    public Long getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(Long refundStatus) {
        this.refundStatus = refundStatus;
    }

    public Boolean getCanCancel() {
        return canCancel;
    }

    public void setCanCancel(Boolean canCancel) {
        this.canCancel = canCancel;
    }

    public Boolean getCanPay() {
        return canPay;
    }

    public void setCanPay(Boolean canPay) {
        this.canPay = canPay;
    }

    public Boolean getCanConfirm() {
        return canConfirm;
    }

    public void setCanConfirm(Boolean canConfirm) {
        this.canConfirm = canConfirm;
    }

    public Boolean getCanRefund() {
        return canRefund;
    }

    public void setCanRefund(Boolean canRefund) {
        this.canRefund = canRefund;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Long getRefundId() {
        return refundId;
    }

    public void setRefundId(Long refundId) {
        this.refundId = refundId;
    }

    public Long getRefundOrderId() {
        return refundOrderId;
    }

    public void setRefundOrderId(Long refundOrderId) {
        this.refundOrderId = refundOrderId;
    }

    public Long getRefundUserId() {
        return refundUserId;
    }

    public void setRefundUserId(Long refundUserId) {
        this.refundUserId = refundUserId;
    }

    public Integer getRefundType() {
        return refundType;
    }

    public void setRefundType(Integer refundType) {
        this.refundType = refundType;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getRefundEvidence() {
        return refundEvidence;
    }

    public void setRefundEvidence(String refundEvidence) {
        this.refundEvidence = refundEvidence;
    }

    public String getRefundAdminRemark() {
        return refundAdminRemark;
    }

    public void setRefundAdminRemark(String refundAdminRemark) {
        this.refundAdminRemark = refundAdminRemark;
    }

    public Date getRefundCreateTime() {
        return refundCreateTime;
    }

    public void setRefundCreateTime(Date refundCreateTime) {
        this.refundCreateTime = refundCreateTime;
    }

    public Date getRefundUpdateTime() {
        return refundUpdateTime;
    }

    public void setRefundUpdateTime(Date refundUpdateTime) {
        this.refundUpdateTime = refundUpdateTime;
    }
}
