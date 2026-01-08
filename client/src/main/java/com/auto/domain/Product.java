package com.auto.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.auto.common.annotation.Excel;
import com.auto.common.core.domain.BaseEntity;

/**
 * 商品对象 product
 *
 * @author auto
 * @date 2026-01-07
 */
public class Product extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 分类ID */
    @Excel(name = "分类ID")
    private Long categoryId;

    /** 品牌名称 */
    @Excel(name = "品牌名称")
    private String brand;

    /** 商品名称 */
    @Excel(name = "商品名称")
    private String name;

    /** 主图 */
    @Excel(name = "主图")
    private String mainImage;

    /** 附图（多个图片路径，JSON格式） */
    @Excel(name = "附图", readConverterExp = "多=个图片路径，JSON格式")
    private String subImages;

    /** 商品详情（HTML/富文本） */
    @Excel(name = "商品详情", readConverterExp = "H=TML/富文本")
    private String detail;

    /** 规格参数（JSON格式） */
    @Excel(name = "规格参数", readConverterExp = "J=SON格式")
    private String spec;

    /** 适配车型（JSON格式） */
    @Excel(name = "适配车型", readConverterExp = "J=SON格式")
    private String fitCarModel;

    /** 价格 */
    @Excel(name = "价格")
    private BigDecimal price;

    /** 库存 */
    @Excel(name = "库存")
    private Long stock;

    /** 库存预警值 */
    @Excel(name = "库存预警值")
    private Long warnStock;

    /** 销量 */
    @Excel(name = "销量")
    private Long sales;

    /** 是否热销 */
    @Excel(name = "是否热销")
    private Integer isHot;

    /** 是否新品 */
    @Excel(name = "是否新品")
    private Integer isNew;

    /** 状态（0下架，1上架） */
    @Excel(name = "状态", readConverterExp = "0=下架，1上架")
    private Integer status;

    /** 排序字段（用于查询，不映射到数据库） */
    private String orderBy;

    /** 最低价格（用于查询，不映射到数据库） */
    private BigDecimal priceMin;

    /** 最高价格（用于查询，不映射到数据库） */
    private BigDecimal priceMax;

    public String getOrderBy()
    {
        return orderBy;
    }

    public void setOrderBy(String orderBy)
    {
        this.orderBy = orderBy;
    }

    public BigDecimal getPriceMin()
    {
        return priceMin;
    }

    public void setPriceMin(BigDecimal priceMin)
    {
        this.priceMin = priceMin;
    }

    public BigDecimal getPriceMax()
    {
        return priceMax;
    }

    public void setPriceMax(BigDecimal priceMax)
    {
        this.priceMax = priceMax;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }

    public void setCategoryId(Long categoryId)
    {
        this.categoryId = categoryId;
    }

    public Long getCategoryId()
    {
        return categoryId;
    }

    public void setBrand(String brand)
    {
        this.brand = brand;
    }

    public String getBrand()
    {
        return brand;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setMainImage(String mainImage)
    {
        this.mainImage = mainImage;
    }

    public String getMainImage()
    {
        return mainImage;
    }

    public void setSubImages(String subImages)
    {
        this.subImages = subImages;
    }

    public String getSubImages()
    {
        return subImages;
    }

    public void setDetail(String detail)
    {
        this.detail = detail;
    }

    public String getDetail()
    {
        return detail;
    }

    public void setSpec(String spec)
    {
        this.spec = spec;
    }

    public String getSpec()
    {
        return spec;
    }

    public void setFitCarModel(String fitCarModel)
    {
        this.fitCarModel = fitCarModel;
    }

    public String getFitCarModel()
    {
        return fitCarModel;
    }

    public void setPrice(BigDecimal price)
    {
        this.price = price;
    }

    public BigDecimal getPrice()
    {
        return price;
    }

    public void setStock(Long stock)
    {
        this.stock = stock;
    }

    public Long getStock()
    {
        return stock;
    }

    public void setWarnStock(Long warnStock)
    {
        this.warnStock = warnStock;
    }

    public Long getWarnStock()
    {
        return warnStock;
    }

    public void setSales(Long sales)
    {
        this.sales = sales;
    }

    public Long getSales()
    {
        return sales;
    }

    public void setIsHot(Integer isHot)
    {
        this.isHot = isHot;
    }

    public Integer getIsHot()
    {
        return isHot;
    }

    public void setIsNew(Integer isNew)
    {
        this.isNew = isNew;
    }

    public Integer getIsNew()
    {
        return isNew;
    }

    public void setStatus(Integer status)
    {
        this.status = status;
    }

    public Integer getStatus()
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("categoryId", getCategoryId())
                .append("brand", getBrand())
                .append("name", getName())
                .append("mainImage", getMainImage())
                .append("subImages", getSubImages())
                .append("detail", getDetail())
                .append("spec", getSpec())
                .append("fitCarModel", getFitCarModel())
                .append("price", getPrice())
                .append("stock", getStock())
                .append("warnStock", getWarnStock())
                .append("sales", getSales())
                .append("isHot", getIsHot())
                .append("isNew", getIsNew())
                .append("status", getStatus())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
