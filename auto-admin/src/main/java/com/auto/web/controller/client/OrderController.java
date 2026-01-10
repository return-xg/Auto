package com.auto.web.controller.client;

import com.auto.common.core.domain.AjaxResult;
import com.auto.domain.dto.CreateOrderDTO;
import com.auto.domain.dto.RefundApplyDTO;
import com.auto.domain.vo.LogisticsVO;
import com.auto.domain.vo.OrderVO;
import com.auto.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private IOrderService orderService;

    @PostMapping("/create")
    public AjaxResult createOrder(@RequestBody CreateOrderDTO createOrderDTO) {
        try {
            OrderVO orderVO = orderService.createOrder(createOrderDTO);
            return AjaxResult.success("订单创建成功", orderVO);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public AjaxResult getOrderById(@PathVariable Long id) {
        try {
            OrderVO orderVO = orderService.getOrderById(id);
            return AjaxResult.success(orderVO);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @GetMapping("/orderNo/{orderNo}")
    public AjaxResult getOrderByOrderNo(@PathVariable String orderNo) {
        try {
            OrderVO orderVO = orderService.getOrderByOrderNo(orderNo);
            return AjaxResult.success(orderVO);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @GetMapping("/list")
    public AjaxResult getOrderList(@RequestParam Long userId, @RequestParam(required = false) Long status) {
        try {
            List<OrderVO> orderVOList;
            if (status != null) {
                orderVOList = orderService.getOrderList(userId, status);
            } else {
                orderVOList = orderService.getOrderListByUserId(userId);
            }
            return AjaxResult.success(orderVOList);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PostMapping("/pay")
    public AjaxResult payOrder(@RequestParam Long orderId, @RequestParam Integer payType, @RequestParam Boolean success) {
        try {
            OrderVO orderVO = orderService.payOrder(orderId, payType, success);
            if (success) {
                return AjaxResult.success("支付成功", orderVO);
            } else {
                return AjaxResult.error("支付失败");
            }
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PostMapping("/cancel")
    public AjaxResult cancelOrder(@RequestParam Long orderId, @RequestParam(required = false) String reason) {
        try {
            OrderVO orderVO = orderService.cancelOrder(orderId, reason);
            return AjaxResult.success("订单取消成功", orderVO);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PostMapping("/confirm")
    public AjaxResult confirmOrder(@RequestParam Long orderId) {
        try {
            OrderVO orderVO = orderService.confirmOrder(orderId);
            return AjaxResult.success("确认收货成功", orderVO);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @GetMapping("/logistics/{orderId}")
    public AjaxResult getLogistics(@PathVariable Long orderId) {
        try {
            LogisticsVO logisticsVO = orderService.getLogistics(orderId);
            return AjaxResult.success(logisticsVO);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PostMapping("/refund")
    public AjaxResult applyRefund(@RequestBody RefundApplyDTO refundApplyDTO) {
        try {
            OrderVO orderVO = orderService.applyRefund(refundApplyDTO);
            return AjaxResult.success("退款申请提交成功", orderVO);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }
}
