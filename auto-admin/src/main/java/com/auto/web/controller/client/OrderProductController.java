package com.auto.web.controller.client;

import com.auto.common.core.domain.AjaxResult;
import com.auto.domain.OrderProduct;
import com.auto.domain.vo.OrderProductVO;
import com.auto.service.IOrderProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单商品管理控制器
 * 提供订单商品的增删改查等操作接口
 */
@RestController
@RequestMapping("/order-product")
public class OrderProductController {

    @Autowired
    private IOrderProductService orderProductService;

    /**
     * 根据ID获取订单商品详情
     *
     * @param id 订单商品ID
     * @return 订单商品详情视图对象
     */
    @GetMapping("/{id}")
    public AjaxResult getOrderProductById(@PathVariable Long id) {
        try {
            OrderProductVO orderProductVO = orderProductService.getOrderProductById(id);
            return AjaxResult.success(orderProductVO);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 根据订单ID获取订单商品列表
     *
     * @param orderId 订单ID
     * @return 订单商品列表视图对象
     */
    @GetMapping("/list/orderId/{orderId}")
    public AjaxResult getOrderProductListByOrderId(@PathVariable Long orderId) {
        try {
            List<OrderProductVO> orderProductVOList = orderProductService.getOrderProductListByOrderId(orderId);
            return AjaxResult.success(orderProductVOList);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 获取订单商品列表（支持条件查询）
     *
     * @param orderProduct 订单商品查询条件
     * @return 订单商品列表视图对象
     */
    @GetMapping("/list")
    public AjaxResult getOrderProductList(OrderProduct orderProduct) {
        try {
            List<OrderProductVO> orderProductVOList = orderProductService.getOrderProductList(orderProduct);
            return AjaxResult.success(orderProductVOList);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 添加新的订单商品
     *
     * @param orderProduct 订单商品实体
     * @return 操作结果
     */
    @PostMapping("/add")
    public AjaxResult addOrderProduct(@RequestBody OrderProduct orderProduct) {
        try {
            int result = orderProductService.insertOrderProduct(orderProduct);
            return result > 0 ? AjaxResult.success("添加成功") : AjaxResult.error("添加失败");
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 更新订单商品信息
     *
     * @param orderProduct 订单商品实体
     * @return 操作结果
     */
    @PutMapping("/update")
    public AjaxResult updateOrderProduct(@RequestBody OrderProduct orderProduct) {
        try {
            int result = orderProductService.updateOrderProduct(orderProduct);
            return result > 0 ? AjaxResult.success("修改成功") : AjaxResult.error("修改失败");
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 根据ID删除订单商品
     *
     * @param id 订单商品ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public AjaxResult deleteOrderProduct(@PathVariable Long id) {
        try {
            int result = orderProductService.deleteOrderProductById(id);
            return result > 0 ? AjaxResult.success("删除成功") : AjaxResult.error("删除失败");
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 批量删除订单商品
     *
     * @param ids 订单商品ID数组
     * @return 操作结果
     */
    @DeleteMapping("/batch")
    public AjaxResult deleteOrderProductByIds(@RequestBody Long[] ids) {
        try {
            int result = orderProductService.deleteOrderProductByIds(ids);
            return result > 0 ? AjaxResult.success("删除成功") : AjaxResult.error("删除失败");
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 根据订单ID删除订单商品
     *
     * @param orderId 订单ID
     * @return 操作结果
     */
    @DeleteMapping("/orderId/{orderId}")
    public AjaxResult deleteOrderProductByOrderId(@PathVariable Long orderId) {
        try {
            int result = orderProductService.deleteOrderProductByOrderId(orderId);
            return result > 0 ? AjaxResult.success("删除成功") : AjaxResult.error("删除失败");
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }
}
