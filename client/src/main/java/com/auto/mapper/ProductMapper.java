package com.auto.mapper;

import java.util.List;
import com.auto.domain.Product;

/**
 * 商品Mapper接口
 *
 * @author auto
 * @date 2026-01-07
 */
public interface ProductMapper
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
     * 根据分类查询商品列表
     *
     * @param categoryId 分类ID
     * @return 商品集合
     */
    public List<Product> selectProductListByCategory(Long categoryId);

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
     * 删除商品
     *
     * @param id 商品主键
     * @return 结果
     */
    public int deleteProductById(Long id);

    /**
     * 批量删除商品
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteProductByIds(Long[] ids);
    
    /**
     * 批量上架商品
     *
     * @param ids 商品主键集合
     * @return 结果
     */
    public int batchPutOnSale(Long[] ids);
    
    /**
     * 批量下架商品
     *
     * @param ids 商品主键集合
     * @return 结果
     */
    public int batchPutOffSale(Long[] ids);
}
