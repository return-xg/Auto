package com.auto.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.auto.common.utils.DateUtils;
import com.auto.domain.Product;
import com.auto.mapper.ProductMapper;
import com.auto.service.IProductService;
import com.alibaba.fastjson2.JSON;

/**
 * 商品Service业务层处理
 *
 * @author auto
 * @date 2026-01-07
 */
@Service
public class ProductServiceImpl implements IProductService
{
    @Autowired
    private ProductMapper productMapper;

    /**
     * 查询商品
     *
     * @param id 商品主键
     * @return 商品
     */
    @Override
    public Product selectProductById(Long id)
    {
        return productMapper.selectProductById(id);
    }

    /**
     * 查询商品列表
     *
     * @param product 商品
     * @return 商品
     */
    @Override
    public List<Product> selectProductList(Product product)
    {
        return productMapper.selectProductList(product);
    }

    /**
     * 搜索商品（多维度）
     *
     * @param product 商品查询条件
     * @return 商品集合
     */
    @Override
    public List<Product> searchProductList(Product product)
    {
        return productMapper.searchProductList(product);
    }

    /**
     * 获取筛选条件（品牌、车型等）
     *
     * @param categoryId 分类ID
     * @return 筛选条件
     */
    @Override
    public Object getFilterOptions(Long categoryId)
    {
        List<Product> products = productMapper.selectProductListByCategory(categoryId);
        
        Set<String> brands = new HashSet<>();
        Set<String> carModels = new HashSet<>();
        Set<String> years = new HashSet<>();
        
        for (Product product : products)
        {
            if (product.getBrand() != null && !product.getBrand().isEmpty())
            {
                brands.add(product.getBrand());
            }
            
            // 解析适配车型JSON
            if (product.getFitCarModel() != null && !product.getFitCarModel().isEmpty())
            {
                try
                {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> fitCars = (List<Map<String, Object>>) (List<?>) JSON.parseArray(product.getFitCarModel(), Map.class);
                    for (Map<String, Object> car : fitCars)
                    {
                        if (car.get("model") != null)
                        {
                            carModels.add(car.get("model").toString());
                        }
                        if (car.get("year") != null)
                        {
                            years.add(car.get("year").toString());
                        }
                    }
                }
                catch (Exception e)
                {
                    // 解析失败，忽略
                }
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("brands", new ArrayList<>(brands));
        result.put("carModels", new ArrayList<>(carModels));
        result.put("years", new ArrayList<>(years));
        
        return result;
    }

    /**
     * 新增商品
     *
     * @param product 商品
     * @return 结果
     */
    @Override
    public int insertProduct(Product product)
    {
        // 设置默认值
        if (product.getStatus() == null)
        {
            product.setStatus(1); // 默认上架
        }
        if (product.getSales() == null)
        {
            product.setSales(0L); // 默认销量为0
        }
        if (product.getIsHot() == null)
        {
            product.setIsHot(0); // 默认非热销
        }
        if (product.getIsNew() == null)
        {
            product.setIsNew(0); // 默认非新品
        }
        if (product.getStock() == null)
        {
            product.setStock(0L); // 默认库存为0
        }
        if (product.getWarnStock() == null)
        {
            product.setWarnStock(10L); // 默认库存预警值为10
        }
        product.setCreateTime(DateUtils.getNowDate());
        return productMapper.insertProduct(product);
    }

    /**
     * 修改商品
     *
     * @param product 商品
     * @return 结果
     */
    @Override
    public int updateProduct(Product product)
    {
        product.setUpdateTime(DateUtils.getNowDate());
        return productMapper.updateProduct(product);
    }

    /**
     * 批量删除商品
     *
     * @param ids 需要删除的商品主键集合
     * @return 结果
     */
    @Override
    public int deleteProductByIds(Long[] ids)
    {
        return productMapper.deleteProductByIds(ids);
    }

    /**
     * 删除商品信息
     *
     * @param id 商品主键
     * @return 结果
     */
    @Override
    public int deleteProductById(Long id)
    {
        return productMapper.deleteProductById(id);
    }
    
    @Override
    public int putOnSale(Long id)
    {
        Product product = new Product();
        product.setId(id);
        product.setStatus(1); // 1表示上架
        product.setUpdateTime(DateUtils.getNowDate());
        return productMapper.updateProduct(product);
    }
    
    @Override
    public int putOffSale(Long id)
    {
        Product product = new Product();
        product.setId(id);
        product.setStatus(0); // 0表示下架
        product.setUpdateTime(DateUtils.getNowDate());
        return productMapper.updateProduct(product);
    }
    
    @Override
    public int batchPutOnSale(Long[] ids)
    {
        return productMapper.batchPutOnSale(ids);
    }
    
    @Override
    public int batchPutOffSale(Long[] ids)
    {
        return productMapper.batchPutOffSale(ids);
    }
}
