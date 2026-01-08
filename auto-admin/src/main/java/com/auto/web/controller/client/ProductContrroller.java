package com.auto.web.controller.client;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.auto.common.core.controller.BaseController;
import com.auto.common.core.domain.AjaxResult;
import com.auto.common.core.page.TableDataInfo;
import com.auto.domain.Product;
import com.auto.service.IProductService;

/**
 * 商品Controller
 *
 * @author auto
 * @date 2026-01-07
 */
@RestController
@RequestMapping("/product")
public class ProductContrroller extends BaseController
{
    @Autowired
    private IProductService productService;

    /**
     * 查询商品列表（分页、排序）
     */
    @GetMapping("/list")
    public TableDataInfo list(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String orderBy,
            @RequestParam(required = false) BigDecimal priceMin,
            @RequestParam(required = false) BigDecimal priceMax,
            @RequestParam(required = false) String carModel,
            @RequestParam(required = false) String year)
    {
        startPage();
        Product product = new Product();
        product.setCategoryId(categoryId);
        product.setBrand(brand);
        product.setName(name);
        product.setOrderBy(orderBy);
        product.setPriceMin(priceMin);
        product.setPriceMax(priceMax);
        if (carModel != null || year != null)
        {
            // 构建适配车型查询条件
            String fitCarModel = "";
            if (carModel != null)
            {
                fitCarModel = carModel;
            }
            if (year != null)
            {
                fitCarModel += (fitCarModel.isEmpty() ? "" : ",") + year;
            }
            product.setFitCarModel(fitCarModel);
        }
        List<Product> list = productService.searchProductList(product);
        return getDataTable(list);
    }

    /**
     * 获取商品详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        Product product = productService.selectProductById(id);
        if (product == null)
        {
            return error("商品不存在");
        }
        return success(product);
    }

    /**
     * 多维度智能搜索
     */
    @GetMapping("/search")
    public TableDataInfo search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String carModel,
            @RequestParam(required = false) String year,
            @RequestParam(required = false) String orderBy)
    {
        startPage();
        Product product = new Product();
        product.setName(keyword);
        product.setBrand(brand);
        if (carModel != null || year != null)
        {
            String fitCarModel = "";
            if (carModel != null)
            {
                fitCarModel = carModel;
            }
            if (year != null)
            {
                fitCarModel += (fitCarModel.isEmpty() ? "" : ",") + year;
            }
            product.setFitCarModel(fitCarModel);
        }
        product.setOrderBy(orderBy);
        List<Product> list = productService.searchProductList(product);
        return getDataTable(list);
    }

    /**
     * 获取筛选选项（品牌、车型、年份等）
     */
    @GetMapping("/filter-options")
    public AjaxResult getFilterOptions(@RequestParam(required = false) Long categoryId)
    {
        Object options = productService.getFilterOptions(categoryId);
        return success(options);
    }

    /**
     * 新增商品
     */
    @PostMapping
    public AjaxResult add(@RequestBody Product product)
    {
        return toAjax(productService.insertProduct(product));
    }

    /**
     * 修改商品
     */
    @PutMapping
    public AjaxResult edit(@RequestBody Product product)
    {
        return toAjax(productService.updateProduct(product));
    }

    /**
     * 删除商品
     */
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(productService.deleteProductByIds(ids));
    }
    
    /**
     * 商品上架
     */
    @PutMapping("/{id}/putOnSale")
    public AjaxResult putOnSale(@PathVariable Long id)
    {
        return toAjax(productService.putOnSale(id));
    }
    
    /**
     * 商品下架
     */
    @PutMapping("/{id}/putOffSale")
    public AjaxResult putOffSale(@PathVariable Long id)
    {
        return toAjax(productService.putOffSale(id));
    }
    
    /**
     * 商品批量上架
     */
    @PutMapping("/batchPutOnSale")
    public AjaxResult batchPutOnSale(@RequestBody Long[] ids)
    {
        return toAjax(productService.batchPutOnSale(ids));
    }
    
    /**
     * 商品批量下架
     */
    @PutMapping("/batchPutOffSale")
    public AjaxResult batchPutOffSale(@RequestBody Long[] ids)
    {
        return toAjax(productService.batchPutOffSale(ids));
    }
    
    /**
     * 修改商品库存
     */
    @PutMapping("/{id}/updateStock")
    public AjaxResult updateStock(@PathVariable Long id, @RequestParam Long stock)
    {
        if (stock < 0)
        {
            return error("库存不能为负数");
        }
        Product product = new Product();
        product.setId(id);
        product.setStock(stock);
        return toAjax(productService.updateProduct(product));
    }
    
    /**
     * 设置库存预警值
     */
    @PutMapping("/{id}/updateWarnStock")
    public AjaxResult updateWarnStock(@PathVariable Long id, @RequestParam Long warnStock)
    {
        if (warnStock < 0)
        {
            return error("库存预警值不能为负数");
        }
        Product product = new Product();
        product.setId(id);
        product.setWarnStock(warnStock);
        return toAjax(productService.updateProduct(product));
    }
}
