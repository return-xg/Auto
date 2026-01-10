package com.auto.web.controller.client;

import com.auto.common.core.domain.AjaxResult;
import com.auto.domain.OrderProduct;
import com.auto.domain.vo.OrderProductVO;
import com.auto.service.IOrderProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order-product")
public class OrderProductController {

    @Autowired
    private IOrderProductService orderProductService;

    @GetMapping("/{id}")
    public AjaxResult getOrderProductById(@PathVariable Long id) {
        try {
            OrderProductVO orderProductVO = orderProductService.getOrderProductById(id);
            return AjaxResult.success(orderProductVO);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @GetMapping("/list/orderId/{orderId}")
    public AjaxResult getOrderProductListByOrderId(@PathVariable Long orderId) {
        try {
            List<OrderProductVO> orderProductVOList = orderProductService.getOrderProductListByOrderId(orderId);
            return AjaxResult.success(orderProductVOList);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @GetMapping("/list")
    public AjaxResult getOrderProductList(OrderProduct orderProduct) {
        try {
            List<OrderProductVO> orderProductVOList = orderProductService.getOrderProductList(orderProduct);
            return AjaxResult.success(orderProductVOList);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PostMapping("/add")
    public AjaxResult addOrderProduct(@RequestBody OrderProduct orderProduct) {
        try {
            int result = orderProductService.insertOrderProduct(orderProduct);
            return result > 0 ? AjaxResult.success("添加成功") : AjaxResult.error("添加失败");
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PutMapping("/update")
    public AjaxResult updateOrderProduct(@RequestBody OrderProduct orderProduct) {
        try {
            int result = orderProductService.updateOrderProduct(orderProduct);
            return result > 0 ? AjaxResult.success("修改成功") : AjaxResult.error("修改失败");
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public AjaxResult deleteOrderProduct(@PathVariable Long id) {
        try {
            int result = orderProductService.deleteOrderProductById(id);
            return result > 0 ? AjaxResult.success("删除成功") : AjaxResult.error("删除失败");
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @DeleteMapping("/batch")
    public AjaxResult deleteOrderProductByIds(@RequestBody Long[] ids) {
        try {
            int result = orderProductService.deleteOrderProductByIds(ids);
            return result > 0 ? AjaxResult.success("删除成功") : AjaxResult.error("删除失败");
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

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
