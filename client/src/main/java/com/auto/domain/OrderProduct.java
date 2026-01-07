package com.auto.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.auto.common.annotation.Excel;
import com.auto.common.core.domain.BaseEntity;

/**
 * 订单商品对象 order_product
 *
 * @author auto
 * @date 2026-01-07
 */
public class OrderProduct extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 订单ID */
    @Excel(name = "订单ID")
    private Long orderId;

    /** 商品ID */
    @Excel(name = "商品ID")
    private Long productId;

    /** 商品名称（快照） */
    @Excel(name = "商品名称", readConverterExp = "快=照")
    private String productName;

    /** 商品图片（快照） */
    @Excel(name = "商品图片", readConverterExp = "快=照")
    private String productImage;

    /** 商品规格（快照，JSON格式） */
    @Excel(name = "商品规格", readConverterExp = "快=照，JSON格式")
    private String productSpec;

    /** 商品单价（快照） */
    @Excel(name = "商品单价", readConverterExp = "快=照")
    private BigDecimal price;

    /** 数量 */
    @Excel(name = "数量")
    private Long quantity;

    /** 商品总价 */
    @Excel(name = "商品总价")
    private BigDecimal totalPrice;

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

    public void setProductId(Long productId)
    {
        this.productId = productId;
    }

    public Long getProductId()
    {
        return productId;
    }

    public void setProductName(String productName)
    {
        this.productName = productName;
    }

    public String getProductName()
    {
        return productName;
    }

    public void setProductImage(String productImage)
    {
        this.productImage = productImage;
    }

    public String getProductImage()
    {
        return productImage;
    }

    public void setProductSpec(String productSpec)
    {
        this.productSpec = productSpec;
    }

    public String getProductSpec()
    {
        return productSpec;
    }

    public void setPrice(BigDecimal price)
    {
        this.price = price;
    }

    public BigDecimal getPrice()
    {
        return price;
    }

    public void setQuantity(Long quantity)
    {
        this.quantity = quantity;
    }

    public Long getQuantity()
    {
        return quantity;
    }

    public void setTotalPrice(BigDecimal totalPrice)
    {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getTotalPrice()
    {
        return totalPrice;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("orderId", getOrderId())
                .append("productId", getProductId())
                .append("productName", getProductName())
                .append("productImage", getProductImage())
                .append("productSpec", getProductSpec())
                .append("price", getPrice())
                .append("quantity", getQuantity())
                .append("totalPrice", getTotalPrice())
                .toString();
    }
}
