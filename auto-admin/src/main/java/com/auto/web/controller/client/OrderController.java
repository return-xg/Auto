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

/**
 * 订单Controller
 *
 * @author auto
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private IOrderService orderService;

    /**
     * 创建订单
     *
     * @param createOrderDTO 订单创建参数对象，包含商品信息、用户信息、地址等
     * @return AjaxResult 包含创建结果的响应对象
     */
    @PostMapping("/create")
    public AjaxResult createOrder(@RequestBody CreateOrderDTO createOrderDTO) {
        try {
            OrderVO orderVO = orderService.createOrder(createOrderDTO);
            return AjaxResult.success("订单创建成功", orderVO);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 根据ID查询订单详情
     *
     * @param id 订单ID
     * @return AjaxResult 包含订单详细信息的响应对象
     */
    @GetMapping("/{id}")
    public AjaxResult getOrderById(@PathVariable Long id) {
        try {
            OrderVO orderVO = orderService.getOrderById(id);
            return AjaxResult.success(orderVO);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 根据订单号查询订单
     *
     * @param orderNo 订单号
     * @return AjaxResult 包含订单信息的响应对象
     */
    @GetMapping("/orderNo/{orderNo}")
    public AjaxResult getOrderByOrderNo(@PathVariable String orderNo) {
        try {
            OrderVO orderVO = orderService.getOrderByOrderNo(orderNo);
            return AjaxResult.success(orderVO);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 获取用户订单列表
     *
     * @param userId 用户ID
     * @param status 订单状态(可选)，如果不传则返回全部状态的订单
     * @return AjaxResult 包含订单列表的响应对象
     */
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

    /**
     * 订单支付
     *
     * @param orderId 订单ID
     * @param payType 支付方式
     * @param success 支付是否成功
     * @return AjaxResult 包含支付结果的响应对象
     */
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

    /**
     * 取消订单
     *
     * @param orderId 订单ID
     * @param reason 取消原因(可选)
     * @return AjaxResult 包含取消订单后信息的响应对象
     */
    @PostMapping("/cancel")
    public AjaxResult cancelOrder(@RequestParam Long orderId, @RequestParam(required = false) String reason) {
        try {
            OrderVO orderVO = orderService.cancelOrder(orderId, reason);
            return AjaxResult.success("订单取消成功", orderVO);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 确认收货
     *
     * @param orderId 订单ID
     * @return AjaxResult 包含确认收货后订单信息的响应对象
     */
    @PostMapping("/confirm")
    public AjaxResult confirmOrder(@RequestParam Long orderId) {
        try {
            OrderVO orderVO = orderService.confirmOrder(orderId);
            return AjaxResult.success("确认收货成功", orderVO);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 查询订单物流信息
     *
     * @param orderId 订单ID
     * @return AjaxResult 包含物流信息的响应对象
     */
    @GetMapping("/logistics/{orderId}")
    public AjaxResult getLogistics(@PathVariable Long orderId) {
        try {
            LogisticsVO logisticsVO = orderService.getLogistics(orderId);
            return AjaxResult.success(logisticsVO);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 申请退款
     *
     * @param refundApplyDTO 退款申请参数对象
     * @return AjaxResult 包含退款申请结果的响应对象
     */
    @PostMapping("/refund")
    public AjaxResult applyRefund(@RequestBody RefundApplyDTO refundApplyDTO) {
        try {
            OrderVO orderVO = orderService.applyRefund(refundApplyDTO);
            return AjaxResult.success("退款申请提交成功", orderVO);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 取消退款申请
     *
     * @param orderId 订单ID
     * @return AjaxResult 包含取消退款结果的响应对象
     */
    @PostMapping("/cancelRefund")
    public AjaxResult cancelRefund(@RequestParam Long orderId) {
        try {
            OrderVO orderVO = orderService.cancelRefund(orderId);
            return AjaxResult.success("退款申请已取消", orderVO);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 删除订单
     *
     * @param orderId 订单ID
     * @return AjaxResult 包含删除结果的响应对象
     */
    @PostMapping("/delete")
    public AjaxResult deleteOrder(@RequestParam Long orderId) {
        try {
            int result = orderService.deleteOrder(orderId);
            return result > 0 ? AjaxResult.success("删除成功") : AjaxResult.error("删除失败");
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 批量删除订单
     *
     * @param orderIds 订单ID数组
     * @return AjaxResult 包含删除结果的响应对象
     */
    @PostMapping("/deleteBatch")
    public AjaxResult deleteOrderBatch(@RequestBody Long[] orderIds) {
        try {
            int result = orderService.deleteOrderBatch(orderIds);
            return result > 0 ? AjaxResult.success("删除成功") : AjaxResult.error("删除失败");
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }
}
