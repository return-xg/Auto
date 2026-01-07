package com.auto.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.auto.common.annotation.Excel;
import com.auto.common.core.domain.BaseEntity;

/**
 * 退款/退货对象 refund_return
 *
 * @author auto
 * @date 2026-01-07
 */
public class RefundReturn extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 订单ID */
    @Excel(name = "订单ID")
    private Long orderId;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 类型（1退款，2退货） */
    @Excel(name = "类型", readConverterExp = "1=退款，2退货")
    private Integer type;

    /** 退款/退货原因 */
    @Excel(name = "退款/退货原因")
    private String reason;

    /** 退款金额 */
    @Excel(name = "退款金额")
    private BigDecimal amount;

    /** 凭证图片（多个用逗号分隔） */
    @Excel(name = "凭证图片", readConverterExp = "多=个用逗号分隔")
    private String evidence;

    /** 状态（0待审核，1审核通过，2审核拒绝，3退款中，4退款成功，5退货中，6退货完成） */
    @Excel(name = "状态", readConverterExp = "0=待审核，1审核通过，2审核拒绝，3退款中，4退款成功，5退货中，6退货完成")
    private Long status;

    /** 管理员备注 */
    @Excel(name = "管理员备注")
    private String adminRemark;

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

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setType(Integer type)
    {
        this.type = type;
    }

    public Integer getType()
    {
        return type;
    }

    public void setReason(String reason)
    {
        this.reason = reason;
    }

    public String getReason()
    {
        return reason;
    }

    public void setAmount(BigDecimal amount)
    {
        this.amount = amount;
    }

    public BigDecimal getAmount()
    {
        return amount;
    }

    public void setEvidence(String evidence)
    {
        this.evidence = evidence;
    }

    public String getEvidence()
    {
        return evidence;
    }

    public void setStatus(Long status)
    {
        this.status = status;
    }

    public Long getStatus()
    {
        return status;
    }

    public void setAdminRemark(String adminRemark)
    {
        this.adminRemark = adminRemark;
    }

    public String getAdminRemark()
    {
        return adminRemark;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("orderId", getOrderId())
                .append("userId", getUserId())
                .append("type", getType())
                .append("reason", getReason())
                .append("amount", getAmount())
                .append("evidence", getEvidence())
                .append("status", getStatus())
                .append("adminRemark", getAdminRemark())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
