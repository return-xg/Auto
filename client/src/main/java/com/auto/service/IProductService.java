package com.auto.service;

import java.util.List;
import com.auto.domain.Product;

/**
 * 商品Service接口
 *
 * @author auto
 * @date 2026-01-07
 */
public interface IProductService
{
    /**
     * 查询商品
     *
     * @param id 商品主键
     * @return 商品
     */
    public Product selectProductById(Long id);

    /**
     * 查询商品列表
     *
     * @param product 商品
     * @return 商品集合
     */
    public List<Product> selectProductList(Product product);

    /**
     * 搜索商品（多维度）
     *
     * @param product 商品查询条件
     * @return 商品集合
     */
    public List<Product> searchProductList(Product product);

    /**
     * 获取筛选条件（品牌、车型等）
     *
     * @param categoryId 分类ID
     * @return 筛选条件
     */
    public Object getFilterOptions(Long categoryId);

    /**
     * 新增商品
     *
     * @param product 商品
     * @return 结果
     */
    public int insertProduct(Product product);

    /**
     * 修改商品
     *
     * @param product 商品
     * @return 结果
     */
    public int updateProduct(Product product);

    /**
     * 批量删除商品
     *
     * @param ids 需要删除的商品主键集合
     * @return 结果
     */
    public int deleteProductByIds(Long[] ids);

    /**
     * 删除商品信息
     *
     * @param id 商品主键
     * @return 结果
     */
    public int deleteProductById(Long id);
    
    /**
     * 商品上架
     *
     * @param id 商品主键
     * @return 结果
     */
    public int putOnSale(Long id);
    
    /**
     * 商品下架
     *
     * @param id 商品主键
     * @return 结果
     */
    public int putOffSale(Long id);
    
    /**
     * 商品批量上架
     *
     * @param ids 商品主键集合
     * @return 结果
     */
    public int batchPutOnSale(Long[] ids);
    
    /**
     * 商品批量下架
     *
     * @param ids 商品主键集合
     * @return 结果
     */
    public int batchPutOffSale(Long[] ids);
}
