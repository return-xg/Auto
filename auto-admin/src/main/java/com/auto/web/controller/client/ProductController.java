package com.auto.web.controller.client;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import org.springframework.web.multipart.MultipartFile;
import com.alibaba.fastjson2.JSON;
import com.auto.common.config.AutoConfig;
import com.auto.common.core.controller.BaseController;
import com.auto.common.core.domain.AjaxResult;
import com.auto.common.core.page.TableDataInfo;
import com.auto.common.utils.file.FileUploadUtils;
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
public class ProductController extends BaseController
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
            @RequestParam(required = false) String year,
            @RequestParam(required = false) Integer status)
    {
        startPage();
        Product product = new Product();
        product.setCategoryId(categoryId);
        product.setBrand(brand);
        product.setName(name);
        product.setOrderBy(orderBy);
        product.setPriceMin(priceMin);
        product.setPriceMax(priceMax);
        product.setStatus(status);
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
        List<Product> list = productService.selectProductList(product);
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
            @RequestParam(required = false) String orderBy,
            @RequestParam(required = false) Integer status)
    {
        startPage();
        Product product = new Product();
        product.setName(keyword);
        product.setBrand(brand);
        product.setStatus(status);
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
     * @param categoryId 分类ID
     * @param brand 品牌
     * @param name 名称
     * @param mainImage 主图
     * @param subImages 子图
     * @param detail 详情
     * @param spec  规格
     * @param fitCarModel 适配车型
     * @param price  价格
     * @param stock 库存
     * @param warnStock 预警库存
     * @param sales 销量
     * @param isHot 热门
     * @param isNew 新品
     * @param status 状态
     * @return 结果
     */
    @PostMapping
    public AjaxResult add(
            @RequestParam Long categoryId,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) MultipartFile mainImage,
            @RequestParam(required = false) MultipartFile[] subImages,
            @RequestParam(required = false) String detail,
            @RequestParam(required = false) String spec,
            @RequestParam(required = false) String fitCarModel,
            @RequestParam(required = false) BigDecimal price,
            @RequestParam(required = false) Long stock,
            @RequestParam(required = false) Long warnStock,
            @RequestParam(required = false) Long sales,
            @RequestParam(required = false) Integer isHot,
            @RequestParam(required = false) Integer isNew,
            @RequestParam(required = false) Integer status)
    {
        try
        {
            Product product = new Product();
            product.setCategoryId(categoryId);
            product.setBrand(brand);
            product.setName(name);
            product.setDetail(detail);
            product.setSpec(spec);
            product.setFitCarModel(fitCarModel);
            product.setPrice(price);
            product.setStock(stock);
            product.setWarnStock(warnStock);
            product.setSales(sales);
            product.setIsHot(isHot);
            product.setIsNew(isNew);
            product.setStatus(status);

            if (mainImage != null && !mainImage.isEmpty())
            {
                String filePath = AutoConfig.getUploadPath();
                String fileName = FileUploadUtils.upload(filePath, mainImage);
                product.setMainImage(fileName);
            }

            if (subImages != null && subImages.length > 0)
            {
                String filePath = AutoConfig.getUploadPath();
                List<String> subImageUrls = new ArrayList<>();
                for (MultipartFile file : subImages)
                {
                    if (!file.isEmpty())
                    {
                        String fileName = FileUploadUtils.upload(filePath, file);
                        subImageUrls.add(fileName);
                    }
                }
                if (!subImageUrls.isEmpty())
                {
                    product.setSubImages(JSON.toJSONString(subImageUrls));
                }
            }

            return toAjax(productService.insertProduct(product));
        }
        catch (Exception e)
        {
            return error("新增商品失败：" + e.getMessage());
        }
    }

    /**
     * 修改商品
     */
    @PutMapping
    public AjaxResult edit(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) MultipartFile mainImage,
            @RequestParam(required = false) MultipartFile[] subImages,
            @RequestParam(required = false) String detail,
            @RequestParam(required = false) String spec,
            @RequestParam(required = false) String fitCarModel,
            @RequestParam(required = false) BigDecimal price,
            @RequestParam(required = false) Long stock,
            @RequestParam(required = false) Long warnStock,
            @RequestParam(required = false) Long sales,
            @RequestParam(required = false) Integer isHot,
            @RequestParam(required = false) Integer isNew,
            @RequestParam(required = false) Integer status)
    {
        try
        {
            Product product = new Product();
            product.setId(id);
            product.setCategoryId(categoryId);
            product.setBrand(brand);
            product.setName(name);
            product.setDetail(detail);
            product.setSpec(spec);
            product.setFitCarModel(fitCarModel);
            product.setPrice(price);
            product.setStock(stock);
            product.setWarnStock(warnStock);
            product.setSales(sales);
            product.setIsHot(isHot);
            product.setIsNew(isNew);
            product.setStatus(status);

            if (mainImage != null && !mainImage.isEmpty())
            {
                String filePath = AutoConfig.getUploadPath();
                String fileName = FileUploadUtils.upload(filePath, mainImage);
                product.setMainImage(fileName);
            }

            if (subImages != null && subImages.length > 0)
            {
                String filePath = AutoConfig.getUploadPath();
                List<String> subImageUrls = new ArrayList<>();
                for (MultipartFile file : subImages)
                {
                    if (!file.isEmpty())
                    {
                        String fileName = FileUploadUtils.upload(filePath, file);
                        subImageUrls.add(fileName);
                    }
                }
                if (!subImageUrls.isEmpty())
                {
                    product.setSubImages(JSON.toJSONString(subImageUrls));
                }
            }

            return toAjax(productService.updateProduct(product));
        }
        catch (Exception e)
        {
            return error("修改商品失败：" + e.getMessage());
        }
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
